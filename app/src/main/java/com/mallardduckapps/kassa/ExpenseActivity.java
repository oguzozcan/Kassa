package com.mallardduckapps.kassa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mallardduckapps.kassa.adapters.ExpenseItemAdapter;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.ExpenseEvents;
import com.mallardduckapps.kassa.adapters.TypeItemRecyclerViewAdapter;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.objects.TypeItem;
import com.mallardduckapps.kassa.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ExpenseActivity extends BaseSwipeListItemActivity implements BaseSwipeListItemActivity.OnTypeListItemInteractionListener {

    @Override
    protected void setTag() {
        TAG = "ExpenseActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpenseActivity.this, AddNewExpenseActivity.class);
                intent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, categoryId);
                intent.putExtra(AddNewExpenseActivity.POST_OR_UPDATE_KEY, true);
                startActivityForResult(intent, Constants.EXPENSE_CREATED);
            }
        });
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.getBus().register(this);
//        if(categoryId == -1)
            app.getBus().post(new ExpenseEvents.ExpenseListRequest());
//        else
//            app.getBus().post(new ExpenseEvents.ExpenseListByCategoryRequest(categoryId));
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.getBus().unregister(this);
    }

    @Subscribe
    public void onLoadExpenses(ExpenseEvents.ExpenseListResponse expenseListResponse){
        Log.d(TAG, "ON LOAD EXPENSES");
        loadingLayout.setVisibility(View.GONE);
        setExpenseListView(expenseListResponse.getResponse(), this);
    }

    protected void setExpenseListView(Response<ArrayList<Expense>> res, final BaseSwipeListItemActivity activity) {
        if (res != null) {
            if (res.isSuccessful()) {
                ArrayList<Expense> expenseArrayList = res.body();
                final ArrayList<Expense> filteredExpenses = filterExpensesByCategory(expenseArrayList, categoryId);
                Log.d(TAG, "SUCCESFUL " + expenseArrayList.size());
                setTotalExpense(filteredExpenses);
                //callback.refreshJobCountTitle(jobArrayList.size(), sectionNumber);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ViewSwitcher expenseViewSwitcher = (ViewSwitcher) findViewById(R.id.expenseViewSwitcher);
                        if(filteredExpenses.size() > 0 ){
                            final ExpenseItemAdapter adapter = new ExpenseItemAdapter(activity, activity, filteredExpenses, expenseListView);
                            if (expenseListView != null) {
                                expenseListView.setAdapter(adapter);
                                adapter.setUndoOn(true);
                                setUpItemTouchHelper();
                                setUpAnimationDecoratorHelper();
                                expenseViewSwitcher.setDisplayedChild(0);
                                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpense);
                                fab.setVisibility(View.VISIBLE);
                            }
                        }else{
                            Log.d(TAG, "EMPTY STATE");
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
                            LinearLayout containerLayout = (LinearLayout) findViewById(R.id.recylerViewContainer);
                            recyclerView.setLayoutManager(new GridLayoutManager(ExpenseActivity.this, 2));
                            recyclerView.setAdapter(new TypeItemRecyclerViewAdapter(getTypeItems(Constants.CATEGORY_ID_DAILY), ExpenseActivity.this, R.color.daily_type_background));
                            int color = ContextCompat.getColor(ExpenseActivity.this, R.color.daily_type_background);
                            recyclerView.setBackgroundColor(color);
                            containerLayout.setBackgroundColor(color);
                            expenseViewSwitcher.setDisplayedChild(1);
                        }

                    }
                });
            } else {
                app.getBus().post(new ApiErrorEvent(res.code(), res.message(), true));
            }
        } else if (res == null) {
            app.getBus().post(new ApiErrorEvent(0, "Problem!!!", true));

        }
    }

    protected ArrayList<Expense> filterExpensesByCategory(ArrayList<Expense> expenseArrayList, int categoryId){
        ArrayList<Expense> items = new ArrayList<>();
        for(Expense expense : expenseArrayList){
            if(expense.getCategoryId() == categoryId){
                items.add(expense);
            }
        }
        return items;
    }

    protected void setTotalExpense(ArrayList<Expense> expenseArrayList){
        TextView totalExpenseTextView = (TextView) findViewById(R.id.totalExpense);//totalExpense
        double total = 0;
        for(Expense expense : expenseArrayList){
            total += expense.getPrice();
        }
        totalExpenseTextView.setText(String.format("%.2f %s", total, " TL"));//total + " TL");
    }

//    @Subscribe
//    public void onLoadExpensesByCategory(ExpenseEvents.ExpenseListByCategoryResponse expenseListResponse){
//        Log.d(TAG, "ON LOAD EXPENSES");
//        loadingLayout.setVisibility(View.GONE);
//        setExpenseListView(expenseListResponse.getResponse());
//    }

    @Subscribe
    public void onExpenseDeleted(ExpenseEvents.DeleteExpenseResponse expenseResponse){
        Log.d(TAG, "EXpense Deleted set loading visibility gone");
        try{
            String answer = expenseResponse.getItem().body();
            Log.d(TAG, "EXPENSE DELETED answer: " + answer);
        }catch(Exception e){
            e.printStackTrace();
        }
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "ON ACTIVITY RESULT: " + requestCode + "- resultCode: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "ON ACTIVITY RESULT UPDATE PROBLEM");
            return;
        }
        if(resultCode == Constants.EXPENSE_CREATED || resultCode == Constants.EXPENSE_UPDATED){
            app.getBus().post(new ExpenseEvents.ExpenseListRequest());
            loadingLayout.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void removeExpense(final int expenseId) {
        if(expenseId != -1){
            //TODO loadingBar
            Log.d(TAG, "Call delete expense: " + expenseId);
            //loadingLayout.setVisibility(View.VISIBLE);
            app.getBus().post(new ExpenseEvents.DeleteExpenseRequest(expenseId));
        }
    }

    @Override
    public void onItemClick(TypeItem item) {
        Intent intent = new Intent(ExpenseActivity.this, AddNewExpenseActivity.class);
        intent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, categoryId);
        intent.putExtra(AddNewExpenseActivity.POST_OR_UPDATE_KEY, true);
        startActivityForResult(intent, Constants.EXPENSE_CREATED);
    }
}
