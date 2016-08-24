package com.mallardduckapps.kassa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.mallardduckapps.kassa.busevents.ExpenseEvents;
import com.mallardduckapps.kassa.objects.Expense;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.TimeUtils;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.Calendar;

import retrofit2.Response;

public class AddNewExpenseActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    @Override
    protected void setTag() {
        TAG = "AddNewExpenseActivity";
    }

    int selectedCategoryId = - 1;
    DateTime dateTime;
    DateTime lastDateTime;
    RelativeLayout loadingLayout;
    public static final String CATEGORY_ID_KEY ="CATEGORY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expense);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        selectedCategoryId = getIntent().getExtras().getInt(AddNewExpenseActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_DAILY);

        getSupportActionBar().setTitle("Harcama Yarat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpense);
        TextView date = (TextView) findViewById(R.id.date);
        TextView lastDate = (TextView) findViewById(R.id.lastDate);

        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        LinearLayout lastDateLayout = (LinearLayout) findViewById(R.id.lastDateLayout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        dateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(2, true), true);
        lastDateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(15, true), true);

        date.setText(TimeUtils.dtfOutWOTime.print(dateTime));
        lastDate.setText(TimeUtils.dtfOutWOTime.print(lastDateTime));

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //calendar.add(Calendar.DAY_OF_MONTH, 2);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(AddNewExpenseActivity.this, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth() + 2);
                datePicker.setMinDate(calendar);
                datePicker.show(AddNewExpenseActivity.this.getFragmentManager(), "dateLayout");
            }
        });
        lastDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //calendar.add(Calendar.DAY_OF_MONTH, 2);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(AddNewExpenseActivity.this, lastDateTime.getYear(), lastDateTime.getMonthOfYear() - 1, lastDateTime.getDayOfMonth() + 2);
                datePicker.setMinDate(calendar);
                datePicker.show(AddNewExpenseActivity.this.getFragmentManager(), "lastDateLayout");
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace this", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Expense expense = isAnswersValid();
                if(expense != null){
                    Log.d(TAG, "ANSWERS VALID post new expense");
                    loadingLayout.setVisibility(View.VISIBLE);
                    app.getBus().post(new ExpenseEvents.PostExpenseRequest(expense));
                }
            }
        });

        initCategoryId(selectedCategoryId);
    }

    private void initCategoryId(int categoryId){
        RadioButton daily = (RadioButton) findViewById(R.id.dailyCat);
        RadioButton home = (RadioButton) findViewById(R.id.homeCat);
        RadioButton activity = (RadioButton) findViewById(R.id.activityCat);
        RadioButton work = (RadioButton) findViewById(R.id.workCat);
        switch (categoryId){
            case Constants.CATEGORY_ID_DAILY:
                daily.setChecked(true);
                break;
            case Constants.CATEGORY_ID_HOME:
                home.setChecked(true);
                break;
            case Constants.CATEGORY_ID_EVENT:
                activity.setChecked(true);
                break;
            case Constants.CATEGORY_ID_WORK:
                work.setChecked(true);
                break;
        }
    }

    private Expense isAnswersValid(){
        EditText priceEditText = (EditText) findViewById(R.id.priceEditText);
        EditText titleEditText = (EditText) findViewById(R.id.title);
        TextView dateTv = (TextView) findViewById(R.id.date);
        TextView lastDateTv = (TextView) findViewById(R.id.lastDate);
        Expense expense;
        double price;
        if(selectedCategoryId == -1){
            app.showMessage(AddNewExpenseActivity.this, "Lütfen kategori seçiniz.", Toast.LENGTH_SHORT);
            return null;
        }
        try{
           String priceTxt = priceEditText.getText().toString().trim();
           price = Double.parseDouble(priceTxt);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        String title = titleEditText.getText().toString().trim();
        if(title.length() < 1){
            return null;
        }
        expense = new Expense();
        expense.setName(title);
        expense.setCategoryId(selectedCategoryId);
        expense.setPrice(price);
        expense.setDueDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, lastDateTv.getText().toString()));
        expense.setCreateDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, dateTv.getText().toString()));
        //TODO
        expense.setSubCategoryId(1);
        expense.setCurrencyId(1);
        return expense;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.dailyCat:
                if (checked){
                    selectedCategoryId = Constants.CATEGORY_ID_DAILY;
                    break;
                }
            case R.id.homeCat:
                if (checked){
                    selectedCategoryId = Constants.CATEGORY_ID_HOME;
                    break;
                }
            case R.id.activityCat:
                if (checked){
                    selectedCategoryId = Constants.CATEGORY_ID_EVENT;
                    break;
                }
            case R.id.workCat:
                if (checked){
                    selectedCategoryId = Constants.CATEGORY_ID_WORK;
                    break;
                }
        }
    }

    @Subscribe
    public void onExpensePosted(ExpenseEvents.PostExpenseResponse response){
        Log.d(TAG, "ON EXPENSE POSTED: ");
        if(response != null){
            Response<Expense> expenseResponse = response.getExpense();
            Expense expense = expenseResponse.body();
            if(expense != null){
                loadingLayout.setVisibility(View.GONE);
                finish();
                Log.d(TAG, "FINISH ACTIVITY");
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        app.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.getBus().unregister(this);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        String tag = dialog.getTag();
        String dateText = "" + dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        TextView dateTv = (TextView) findViewById(R.id.date);
        if(tag.equals("lastDateLayout")){
            dateTv = (TextView) findViewById(R.id.lastDate);
            lastDateTime = new DateTime(year, monthOfYear, dayOfMonth, 0 , 0);
        }else{
            dateTime = new DateTime(year, monthOfYear, dayOfMonth, 0 , 0);
        }
        dateTv.setText(TimeUtils.convertSimpleDateToReadableForm(dateText, false));
    }
}
