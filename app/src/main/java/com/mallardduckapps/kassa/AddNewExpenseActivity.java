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
import android.widget.ViewFlipper;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
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

    private int selectedCategoryId = -1;
    private int expenseId;
    private boolean isPostScreen = true;
    private DateTime dateTime;
    //private DateTime lastDateTime;
    private RelativeLayout loadingLayout;
    public static final String CATEGORY_ID_KEY = "CATEGORY_ID";
    public static final String POST_OR_UPDATE_KEY = "IS_POST";
    public static final String EXPENSE_KEY = "EXPENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expense);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle bundle = getIntent().getExtras();
        selectedCategoryId = bundle.getInt(CATEGORY_ID_KEY, Constants.CATEGORY_ID_DAILY);
        isPostScreen = bundle.getBoolean(POST_OR_UPDATE_KEY, true);

        getSupportActionBar().setTitle(isPostScreen ? "Harcama Yarat" : "Harcama Düzenle");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewExpense);
        TextView date = (TextView) findViewById(R.id.date);
        //TextView lastDate = (TextView) findViewById(R.id.lastDate);

        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.parentDateLayout);
        //LinearLayout lastDateLayout = (LinearLayout) findViewById(R.id.lastDateLayout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        dateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(0, true), true);
        //lastDateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(15, true), true);

        //TODO if update convert date time

        date.setText(TimeUtils.dtfOutWOTime.print(dateTime));
        //lastDate.setText(TimeUtils.dtfOutWOTime.print(lastDateTime));

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
//        lastDateLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar calendar = Calendar.getInstance();
//                //calendar.add(Calendar.DAY_OF_MONTH, 2);
//                DatePickerDialog datePicker = DatePickerDialog.newInstance(AddNewExpenseActivity.this, lastDateTime.getYear(), lastDateTime.getMonthOfYear() - 1, lastDateTime.getDayOfMonth() + 2);
//                datePicker.setMinDate(calendar);
//                datePicker.show(AddNewExpenseActivity.this.getFragmentManager(), "lastDateLayout");
//            }
//        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense expense = isAnswersValid();
                if (expense != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(expense);
                    Log.d(TAG, "ANSWERS VALID post new expense: " + json);
                    loadingLayout.setVisibility(View.VISIBLE);
                    if (isPostScreen) {
                        app.getBus().post(new ExpenseEvents.PostExpenseRequest(expense));
                    } else {
                        expense.setId(expenseId);
                        app.getBus().post(new ExpenseEvents.UpdateExpenseRequest(expense));
                    }
                }
            }
        });

        initCategoryId(selectedCategoryId);
        if (!isPostScreen) {
            initExpenseValuesForEdit((Expense) bundle.getParcelable(EXPENSE_KEY));

        }
    }

    private void initExpenseValuesForEdit(Expense expense) {
        expenseId = expense.getId();
        Log.d(TAG, "EXPENSE edit init values : " + expenseId);

        if (expense != null) {
            EditText priceEditText = (EditText) findViewById(R.id.priceEditText);
            EditText titleEditText = (EditText) findViewById(R.id.title);
            TextView dateTv = (TextView) findViewById(R.id.date);

            priceEditText.setText(Double.toString(expense.getPrice()));
            titleEditText.setText(expense.getName());
            dateTv.setText(expense.getCreateDate());
        }
    }

    private void initCategoryId(int categoryId) {
        RadioButton daily = (RadioButton) findViewById(R.id.dailyCat);
        RadioButton home = (RadioButton) findViewById(R.id.homeCat);
        RadioButton activity = (RadioButton) findViewById(R.id.activityCat);
        RadioButton work = (RadioButton) findViewById(R.id.workCat);

        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.typeViewFlipper);

        switch (categoryId) {
            case Constants.CATEGORY_ID_DAILY:
                daily.setChecked(true);
                flipper.setDisplayedChild(0);
                break;
            case Constants.CATEGORY_ID_HOME:
                home.setChecked(true);
                flipper.setDisplayedChild(2);
                break;
            case Constants.CATEGORY_ID_EVENT:
                activity.setChecked(true);
                flipper.setDisplayedChild(1);
                break;
            case Constants.CATEGORY_ID_WORK:
                work.setChecked(true);
                flipper.setDisplayedChild(2);
                break;
        }
    }

    private Expense isAnswersValid() {
        EditText priceEditText = (EditText) findViewById(R.id.priceEditText);
        EditText titleEditText = (EditText) findViewById(R.id.title);
        TextView dateTv = (TextView) findViewById(R.id.date);
        // TextView lastDateTv = (TextView) findViewById(R.id.lastDate);
        Expense expense;
        double price;
        if (selectedCategoryId == -1) {
            app.showMessage(AddNewExpenseActivity.this, "Lütfen kategori seçiniz.", Toast.LENGTH_SHORT);
            return null;
        }
        try {
            String priceTxt = priceEditText.getText().toString().trim();
            price = Double.parseDouble(priceTxt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String title = titleEditText.getText().toString().trim();
        if (title.length() < 1) {
            return null;
        }
        expense = new Expense();
        expense.setName(title);
        expense.setCategoryId(selectedCategoryId);

        expense.setPrice(price);
        //TODO
        expense.setDueDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, dateTv.getText().toString()));
        expense.setCreateDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, dateTv.getText().toString()));
        //TODO
        expense.setSubCategoryId(1);
        expense.setCurrencyId(1);
        return expense;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.typeViewFlipper);
        switch (view.getId()) {
            case R.id.dailyCat:
                if (checked) {
                    selectedCategoryId = Constants.CATEGORY_ID_DAILY;
                    flipper.setDisplayedChild(0);
                    break;
                }
            case R.id.homeCat:
                if (checked) {
                    selectedCategoryId = Constants.CATEGORY_ID_HOME;
                    flipper.setDisplayedChild(2);
                    break;
                }
            case R.id.activityCat:
                if (checked) {
                    selectedCategoryId = Constants.CATEGORY_ID_EVENT;
                    flipper.setDisplayedChild(1);
                    break;
                }
            case R.id.workCat:
                if (checked) {
                    selectedCategoryId = Constants.CATEGORY_ID_WORK;
                    flipper.setDisplayedChild(2);
                    break;
                }
        }
    }

    public void onTypeRadioButtonSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.dailyCat:
                if (checked) {

                    break;
                }
            case R.id.homeCat:
                if (checked) {

                    break;
                }
            case R.id.activityCat:
                if (checked) {

                    break;
                }
            case R.id.workCat:
                if (checked) {
                    break;
                }
        }
    }

    @Subscribe
    public void onExpensePosted(ExpenseEvents.PostExpenseResponse response) {
        Log.d(TAG, "ON EXPENSE POSTED: ");
        if (response != null) {
            Response<Expense> expenseResponse = response.getItem();
            Expense expense = expenseResponse.body();
            if (expense != null) {
                loadingLayout.setVisibility(View.GONE);
                finish();
                Log.d(TAG, "FINISH ACTIVITY");
            }
        }
    }

    @Subscribe
    public void onExpenseUpdated(ExpenseEvents.UpdateExpenseResponse response) {
        Log.d(TAG, "ON EXPENSE POSTED: ");
        if (response != null) {
            Response<Expense> expenseResponse = response.getItem();
            Expense expense = expenseResponse.body();
            if (expense != null) {
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
//        if(tag.equals("lastDateLayout")){
//            dateTv = (TextView) findViewById(R.id.lastDate);
//            lastDateTime = new DateTime(year, monthOfYear, dayOfMonth, 0 , 0);
//        }else{
        dateTime = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
//        }
        dateTv.setText(TimeUtils.convertSimpleDateToReadableForm(dateText, false));
    }
}
