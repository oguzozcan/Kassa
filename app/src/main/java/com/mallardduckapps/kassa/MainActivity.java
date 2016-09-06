package com.mallardduckapps.kassa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mallardduckapps.kassa.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void setTag() {
        TAG = "MainActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().hide();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace this", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        toggle.setDrawerIndicatorEnabled(true);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CircleImageView dailyButton = (CircleImageView) findViewById(R.id.dailyButton);
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent = new Intent(MainActivity.this, ExpenseActivity.class);
                expenseIntent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_DAILY);
                startActivity(expenseIntent);
            }
        });

        CircleImageView homeButton = (CircleImageView) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent = new Intent(MainActivity.this, ExpenseActivity.class);
                expenseIntent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_HOME);
                startActivity(expenseIntent);
            }
        });

        CircleImageView eventButton = (CircleImageView) findViewById(R.id.activityButton);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent = new Intent(MainActivity.this, ExpenseActivity.class);
                expenseIntent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_EVENT);
                startActivity(expenseIntent);
            }
        });

        CircleImageView workButton = (CircleImageView) findViewById(R.id.workButton);
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent = new Intent(MainActivity.this, ExpenseActivity.class);
                expenseIntent.putExtra(AddNewExpenseActivity.CATEGORY_ID_KEY, Constants.CATEGORY_ID_WORK);
                startActivity(expenseIntent);
            }
        });

        ImageView hamburgerIcon = (ImageView) findViewById(R.id.hamburgerIcon);
        hamburgerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void drawerButtonHandler(View view) {
//        Log.d("BASE ACTIVITY", "DRAWER BUTTON CLICKED");
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
//        if (drawer.isDrawerOpen(navView)) {
//            drawer.closeDrawer(navView);
//        } else {
//            // open the drawer
//            drawer.openDrawer(navView);
//        }
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_anasayfa) {
            // Handle the camera action
        } else if (id == R.id.nav_durum) {

        } else if (id == R.id.nav_gelir) {

        } else if (id == R.id.nav_faturalar) {

        } else if (id == R.id.nav_kontaklar) {

        } else if (id == R.id.nav_bildirimler) {

        }else if (id == R.id.nav_ayarlar) {

        }else if (id == R.id.nav_arama) {

        }else if (id == R.id.nav_yardım) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
