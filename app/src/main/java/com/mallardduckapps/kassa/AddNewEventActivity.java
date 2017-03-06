package com.mallardduckapps.kassa;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.mallardduckapps.kassa.adapters.MemberItemRecyclerViewAdapter;
import com.mallardduckapps.kassa.busevents.EventEvents;
import com.mallardduckapps.kassa.objects.Contact;
import com.mallardduckapps.kassa.objects.Event;
import com.mallardduckapps.kassa.objects.EventMember;
import com.mallardduckapps.kassa.objects.Person;
import com.mallardduckapps.kassa.services.ContactsProvider;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.TimeUtils;
import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Response;

import static com.mallardduckapps.kassa.utils.Constants.PICK_CONTACT;

public class AddNewEventActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, MemberItemRecyclerViewAdapter.OnMemberInteractionListener {

    //TODO direct copy paste from add new Event activity change this
    @Override
    protected void setTag() {
        TAG = "AddNewEventActivity";
    }

    private boolean isPostScreen = true;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private RelativeLayout loadingLayout;
    public static final String CATEGORY_ID_KEY = "CATEGORY_ID";
    public static final String POST_OR_UPDATE_KEY = "IS_POST";
    public static final String Event_KEY = "Event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle bundle = getIntent().getExtras();
        //selectedCategoryId = bundle.getInt(CATEGORY_ID_KEY, Constants.CATEGORY_ID_DAILY);
        isPostScreen = bundle.getBoolean(POST_OR_UPDATE_KEY, true);

        getSupportActionBar().setTitle(isPostScreen ? "Etkinlik Yarat" : "Etkinlik Düzenle");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewEvent);
        TextView dateTv = (TextView) findViewById(R.id.startDate);
        TextView endDateTv = (TextView) findViewById(R.id.endDate);

        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.parentDateLayout);
        LinearLayout endDateLayout = (LinearLayout) findViewById(R.id.endDateLayout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        startDateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(0, true), true);
        endDateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(15, true), true);

        //TODO if update convert date time

        dateTv.setText(TimeUtils.dtfOutWOTime.print(startDateTime));
        endDateTv.setText(TimeUtils.dtfOutWOTime.print(endDateTime));

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //calendar.add(Calendar.DAY_OF_MONTH, 2);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(AddNewEventActivity.this, startDateTime.getYear(), startDateTime.getMonthOfYear() - 1, startDateTime.getDayOfMonth() + 2);
                datePicker.setMinDate(calendar);
                datePicker.show(AddNewEventActivity.this.getFragmentManager(), "dateLayout");
            }
        });
        endDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //calendar.add(Calendar.DAY_OF_MONTH, 2);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(AddNewEventActivity.this, endDateTime.getYear(), endDateTime.getMonthOfYear() - 1, endDateTime.getDayOfMonth() + 2);
                datePicker.setMinDate(calendar);
                datePicker.show(AddNewEventActivity.this.getFragmentManager(), "endDateLayout");
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event event = isAnswersValid();
                if (event != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(event);
                    Log.d(TAG, "ANSWERS VALID post new Event: " + json);
                    loadingLayout.setVisibility(View.VISIBLE);
                    if (isPostScreen) {
                        app.getBus().post(new EventEvents.PostEventRequest(event));
                    } else {
                        event.setId(0);
                        // app.getBus().post(new EventEvents.UpdateEventRequest(event));
                    }
                }
            }
        });

        RecyclerView memberRecyclerView = (RecyclerView) findViewById(R.id.memberListView);
        memberRecyclerView.setLayoutManager(new LinearLayoutManager(AddNewEventActivity.this));
        ImageView addMemberButtton = (ImageView) findViewById(R.id.addMemberButton);
        addMemberButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
//        if (!isPostScreen) {
//            initEventValuesForEdit((Event) bundle.getParcelable(Event_KEY));
//        }
    }

//    private void initEventValuesForEdit(Event event) {
//        eventId = event.getId();
//        Log.d(TAG, "Event edit init values : " + eventId);
//
//        if (event != null) {
//            EditText priceEditText = (EditText) findViewById(R.id.priceEditText);
//            EditText titleEditText = (EditText) findViewById(R.id.title);
//            TextView dateTv = (TextView) findViewById(R.id.date);
//
//            //priceEditText.setText(Double.toString(event.getPrice()));
//            titleEditText.setText(event.getName());
//            dateTv.setText(event.getCreateDate());
//        }
//    }

    private Event isAnswersValid() {
        EditText priceEditText = (EditText) findViewById(R.id.priceEditText);
        EditText titleEditText = (EditText) findViewById(R.id.title);
        TextView dateTv = (TextView) findViewById(R.id.startDate);
        TextView endDateTv = (TextView) findViewById(R.id.endDate);
        Event event;
        double price;
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
        event = new Event();
        event.setName(title);
        event.setCategoryId(Constants.CATEGORY_ID_EVENT);

        // event.setPrice(price);
        //TODO
        // event.setDueDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, dateTv.getText().toString()));
        event.setCreateDate(TimeUtils.convertDateTimeFormat(TimeUtils.dtfOutWOTime, TimeUtils.dfISOMS, dateTv.getText().toString()));
        //TODO
        event.setSubCategoryId(1);
        //event.setCurrencyId(1);
        return event;
    }

    @Subscribe
    public void onEventPosted(EventEvents.PostEventResponse response) {
        Log.d(TAG, "ON Event POSTED: ");
        if (response != null) {
            Response<Event> EventResponse = response.getItem();
            Event Event = EventResponse.body();
            if (Event != null) {
                loadingLayout.setVisibility(View.GONE);
                finish();
                Log.d(TAG, "FINISH ACTIVITY");
            }
        }
    }

//    @Subscribe
//    public void onEventUpdated(EventEvents.UpdateEventResponse response) {
//        Log.d(TAG, "ON Event POSTED: ");
//        if (response != null) {
//            Response<Event> EventResponse = response.getItem();
//            Event Event = EventResponse.body();
//            if (Event != null) {
//                loadingLayout.setVisibility(View.GONE);
//                finish();
//                Log.d(TAG, "FINISH ACTIVITY");
//            }
//        }
//    }

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
        if (tag.equals("endDateLayout")) {
            dateTv = (TextView) findViewById(R.id.endDate);
            endDateTime = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
        } else {
            startDateTime = new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
        }
        dateTv.setText(TimeUtils.convertSimpleDateToReadableForm(dateText, false));
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "ON ACTIVITY RESULT UPDATE PROBLEM");
            return;
        }
        switch (reqCode) {
            case (PICK_CONTACT):
                Uri contactData = data.getData();

                ContactsProvider contactsProvider = new ContactsProvider(app);

                Contact contact = contactsProvider.getContact(contactData);
                Log.d(TAG, "RECEIVED CONTACT: " + contact.name + "- contact phone: " + contact.phone);
                Log.d(TAG, "RESULT OK: " + contactData.toString());

                RecyclerView memberRecyclerView = (RecyclerView) findViewById(R.id.memberListView);
                memberRecyclerView.setLayoutManager(new LinearLayoutManager(AddNewEventActivity.this));
                EventMember member = new EventMember();
                Person p = new Person();
                p.setEmail(contact.email);
                p.setPhoneNumber(contact.phone);
                p.setName(contact.name);
                member.setPerson(p);
                member.setPhoneNumber(contact.phone);
                ArrayList<EventMember> members = new ArrayList<>();
                members.add(member);
                memberRecyclerView.setAdapter(new MemberItemRecyclerViewAdapter(members, AddNewEventActivity.this));

                break;
        }
        //super.onActivityResult(reqCode, resultCode, data);
    }

    @Override
    public void onMemberClick(EventMember item) {
        Log.d(TAG, "MEMBER CLICK");
    }
}
