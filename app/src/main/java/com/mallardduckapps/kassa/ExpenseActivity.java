package com.mallardduckapps.kassa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;
import com.mallardduckapps.kassa.adapters.ExpenseItemAdapter;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.ExpenseEvents;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ExpenseActivity extends BaseActivity implements ExpenseItemAdapter.RemoveExpenseCallback {

    private RecyclerView expenseListView;
    private RelativeLayout loadingLayout;
    private int categoryId = -1;

    @Override
    protected void setTag() {
        TAG = "ExpenseActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        expenseListView = (RecyclerView) findViewById(R.id.expenseListView);
        expenseListView.setLayoutManager(mLayoutManager);
        expenseListView.setNestedScrollingEnabled(false);
        expenseListView.setItemAnimator(new DefaultItemAnimator());
        expenseListView.setHasFixedSize(true);
        setUpAnimationDecoratorHelper();

        categoryId = getIntent().getExtras().getInt(AddNewExpenseActivity.CATEGORY_ID_KEY, 1);
        String title = "";
        switch (categoryId){
            case Constants.CATEGORY_ID_DAILY:
                title = getString(R.string.daily);
                break;
            case Constants.CATEGORY_ID_HOME:
                title = getString(R.string.home);
                break;
            case Constants.CATEGORY_ID_EVENT:
                title = getString(R.string.event);
                break;
            case Constants.CATEGORY_ID_WORK:
                title = getString(R.string.work);
                break;
        }
        getSupportActionBar().setTitle(title);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace this", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(ExpenseActivity.this, AddNewExpenseActivity.class);
                intent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, categoryId);
                startActivityForResult(intent, Constants.EXPENSE_CREATED);
            }
        });
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
        setExpenseListView(expenseListResponse.getResponse());
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
            String answer = expenseResponse.getExpense().body();
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
        if(resultCode == Constants.EXPENSE_CREATED){
            app.getBus().post(new ExpenseEvents.ExpenseListRequest());
            loadingLayout.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void setExpenseListView(Response<ArrayList<Expense>> res) {
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
                        if(filteredExpenses.size() > 0 ){
                            final ExpenseItemAdapter adapter = new ExpenseItemAdapter(ExpenseActivity.this, ExpenseActivity.this, filteredExpenses, expenseListView);
                            if (expenseListView != null) {
                                expenseListView.setAdapter(adapter);
                                adapter.setUndoOn(true);
                                setUpItemTouchHelper();
                                setUpAnimationDecoratorHelper();
                            }
                        }else{
                            Log.d(TAG, "EMPTY STATE");
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

    private ArrayList<Expense> filterExpensesByCategory(ArrayList<Expense> expenseArrayList, int categoryId){
        ArrayList<Expense> expenses = new ArrayList<>();
        for(Expense expense : expenseArrayList){
            if(expense.getCategoryId() == categoryId){
                expenses.add(expense);
            }
        }
        return expenses;
    }

    private void setTotalExpense(ArrayList<Expense> expenseArrayList){
        TextView totalExpenseTextView = (TextView) findViewById(R.id.totalExpense);//totalExpense
        double total = 0;
        for(Expense expense : expenseArrayList){
            total += expense.getPrice();
        }
        totalExpenseTextView.setText(String.format("%.2f %s", total, " TL"));//total + " TL");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(ExpenseActivity.this, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) ExpenseActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }
            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ExpenseItemAdapter adapter = (ExpenseItemAdapter)recyclerView.getAdapter();
                if (adapter.isUndoOn() && adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ExpenseItemAdapter adapter = (ExpenseItemAdapter)expenseListView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }
                if (!initiated) {
                    init();
                }
                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(expenseListView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        expenseListView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }
                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {
                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;
                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();
                    // this we need to find out
                    int top = 0;
                    int bottom = 0;
                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }
        });
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
}
