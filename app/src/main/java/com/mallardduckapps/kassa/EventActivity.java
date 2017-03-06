package com.mallardduckapps.kassa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.mallardduckapps.kassa.adapters.EventItemAdapter;
import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.busevents.EventEvents;
import com.mallardduckapps.kassa.adapters.TypeItemRecyclerViewAdapter;
import com.mallardduckapps.kassa.objects.Event;
import com.mallardduckapps.kassa.objects.TypeItem;
import com.mallardduckapps.kassa.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventActivity extends BaseActivity implements BaseSwipeListItemActivity.OnTypeListItemInteractionListener  {

    protected RecyclerView eventListView;
    protected RelativeLayout loadingLayout;
    @Override
    protected void setTag() {
        TAG = "EventActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
        eventListView = (RecyclerView) findViewById(R.id.eventListView);
        eventListView.setLayoutManager(mLayoutManager);
        eventListView.setNestedScrollingEnabled(false);
        eventListView.setItemAnimator(new DefaultItemAnimator());
        eventListView.setHasFixedSize(true);
        getSupportActionBar().setTitle(getString(R.string.event));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewEvent);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventActivity.this, AddNewEventActivity.class);
                intent.putExtra(AddNewEventActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_EVENT);
                intent.putExtra(AddNewEventActivity.POST_OR_UPDATE_KEY, true);
                startActivityForResult(intent, Constants.EVENT_CREATED);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        app.getBus().register(this);
//        if(categoryId == -1)
        app.getBus().post(new EventEvents.EventListRequest());
        Log.d(TAG, "LOAD EVENTS");
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.getBus().unregister(this);
    }

    @Subscribe
    public void onLoadEvents(EventEvents.EventListResponse eventListResponse){
        Log.d(TAG, "ON LOAD Events");
        loadingLayout.setVisibility(View.GONE);
        Response<ArrayList<Event>> response = eventListResponse.getResponse();
        if(response != null){
           if(response.isSuccessful()){
                setEventListView(response);
            }else {
               app.getBus().post(new ApiErrorEvent(response.code(), response.message(), true));
           }
        } else {
            app.getBus().post(new ApiErrorEvent(0, "Problem!!!", true));
        }
    }

    protected void setEventListView(Response<ArrayList<Event>> res) {
        if (res != null) {
            if (res.isSuccessful()) {
                final ArrayList<Event> eventArrayList = res.body();
                Log.d(TAG, "SUCCESFUL " + eventArrayList.size());
                //setTotalExpense(filteredExpenses);
                //callback.refreshJobCountTitle(jobArrayList.size(), sectionNumber);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ViewSwitcher eventViewSwitcher = (ViewSwitcher) findViewById(R.id.eventViewSwitcher);
                        if(eventArrayList.size() > 0 ){
                            final EventItemAdapter adapter = new EventItemAdapter(EventActivity.this, eventArrayList, eventListView);
                            if (eventListView != null) {
                                eventListView.setAdapter(adapter);
                                eventViewSwitcher.setDisplayedChild(0);
                            }
                            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addNewEvent);
                            fab.setVisibility(View.VISIBLE);
                        }else{
                            Log.d(TAG, "EMPTY STATE");
                            //TODO
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
                            LinearLayout containerLayout = (LinearLayout) findViewById(R.id.recylerViewContainer);
                            recyclerView.setLayoutManager(new GridLayoutManager(EventActivity.this, 2));

                            int color = ContextCompat.getColor(EventActivity.this, R.color.event_type_background);
                            recyclerView.setAdapter(new TypeItemRecyclerViewAdapter(getTypeItems(), EventActivity.this, color));
                            //recyclerView.setBackgroundColor(color);
                            containerLayout.setBackgroundColor(color);
                            eventViewSwitcher.setDisplayedChild(1);
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

    protected List<TypeItem> getTypeItems(){
        TypedArray imgs = getResources().obtainTypedArray(R.array.event_type_images);
        TypedArray names= getResources().obtainTypedArray(R.array.event_type_names);
        TypedArray typeIds= getResources().obtainTypedArray(R.array.event_type_ids);
        int size = imgs.length();
        ArrayList<TypeItem> items = new ArrayList<>(size);
        for(int i = 0 ; i < size; i ++){
            TypeItem item = new TypeItem(Constants.CATEGORY_ID_EVENT, names.getString(i), imgs.getResourceId(i, -1), typeIds.getResourceId(i, -1));
            items.add(item);
        }
        imgs.recycle();
        names.recycle();
        return items;
    }

/*    @Subscribe
    public void onEventDeleted(EventEvents.DeleteEventResponse eventResponse){
        Log.d(TAG, "EVENT Deleted set loading visibility gone");
        try{
            String answer = eventResponse.getItem().body();
            Log.d(TAG, "EVENT DELETED answer: " + answer);
        }catch(Exception e){
            e.printStackTrace();
        }
        loadingLayout.setVisibility(View.GONE);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "ON ACTIVITY RESULT: " + requestCode + "- resultCode: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "ON ACTIVITY RESULT UPDATE PROBLEM");
            return;
        }
        if(resultCode == Constants.EVENT_CREATED || resultCode == Constants.EVENT_UPDATED){
            app.getBus().post(new EventEvents.EventListRequest());
            loadingLayout.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(TypeItem item) {
        Intent intent = new Intent(EventActivity.this, AddNewEventActivity.class);
        intent.putExtra(AddNewEventActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_EVENT);
        intent.putExtra(AddNewEventActivity.POST_OR_UPDATE_KEY, true);
        startActivityForResult(intent, Constants.EVENT_CREATED);
    }
}
