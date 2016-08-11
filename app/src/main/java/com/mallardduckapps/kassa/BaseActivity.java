package com.mallardduckapps.kassa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mallardduckapps.kassa.utils.DataSaver;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by oguzemreozcan on 01/08/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = "BaseActivity";
    protected KassaApp app;
    protected DataSaver ds;

    protected abstract void setTag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        app = (KassaApp) getApplication();
        ds = app.getDataSaver();
        setTag();
        //setContentView(R.layout.activity_base);
        //initActionBar(TAG);
    }

    //Should be called after setContentView so each activity shoudl call this seperatly
//    public void initActionBar(String title){
//        ActionBar mActionBar = getSupportActionBar();
////        if(support == null){
////            Log.d(TAG, "SUPPORT ACTIONBAR NULL");
////        }
//        if(mActionBar != null){
//            mActionBar.setDisplayShowHomeEnabled(false);
//            mActionBar.setDisplayShowTitleEnabled(false);
//            LayoutInflater mInflater = LayoutInflater.from(this);
//            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
//            TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.toolbar_title);
//            mTitleTextView.setText(title);
//            mActionBar.setCustomView(mCustomView);
//            mActionBar.setDisplayShowCustomEnabled(true);
//        }else{
//            Log.d(TAG, "ACTIONBAR NULL");
//        }
//    }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public static void setTranslateAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_from_left);
    }

    public static void setBackwardsTranslateAnimation(Activity activity) {
        activity.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            setBackwardsTranslateAnimation(this);
        }
    }
}

