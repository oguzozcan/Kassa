package com.mallardduckapps.kassa;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.mallardduckapps.kassa.busevents.ApiErrorEvent;
import com.mallardduckapps.kassa.services.AuthRestApi;
import com.mallardduckapps.kassa.services.AuthenticationService;
import com.mallardduckapps.kassa.services.ExpenseRestApi;
import com.mallardduckapps.kassa.services.ExpenseService;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.DataSaver;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by oguzemreozcan on 01/08/16.
 */
public class KassaApp extends Application {

    private static Bus mBus;
    private DataSaver dataSaver;
    //public static Locale localeTr = new Locale("tr");
    public boolean isWriteToSdCardPermissionGranted;
    public boolean isGetLocationPremissionGranted = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Retrofit retrofitAuth = createRetrofitObject(Constants.AUTH_URL);
        Retrofit retrofit = createRetrofitObject(Constants.ROOT_URL);
        getBus().register(this);
        getBus().register(new AuthenticationService(getBus(), retrofitAuth.create(AuthRestApi.AuthenticationRestApi.class), retrofit.create(AuthRestApi.RegisterRestApi.class)));
        getBus().register(new ExpenseService(this, retrofit.create(ExpenseRestApi.GetExpensesRestApi.class), retrofit.create(ExpenseRestApi.GetExpensesByCategoryRestApi.class), retrofit.create(ExpenseRestApi.GetExpenseRestApi.class),
                retrofit.create(ExpenseRestApi.PostExpenseRestApi.class), retrofit.create(ExpenseRestApi.DeleteExpenseRestApi.class)));
        getDataSaver();
        // OkHttpClient okHttpClient = new OkHttpClient();
        // File customCacheDirectory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/ArmutHVCache");
        // okHttpClient.setCache(new Cache(customCacheDirectory, 128 * 1024 * 1024)); // Cache = 32MB
        // OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
        // Picasso picasso = new Picasso.Builder(context).downloader(okHttpDownloader).build();
        // picasso.setIndicatorsEnabled(true);
        //picasso.setLoggingEnabled(true);
        //int maxSize = 128 * 1024 * 1024;
        Picasso picasso = new Picasso.Builder(context)
                //.memoryCache(new LruCache(maxSize))
                .build();
        Picasso.setSingletonInstance(picasso);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Museo500-Regular.otf")//DINLight.ttf")// //  //dinbek-regular.ttf
                        .setFontAttrId(R.attr.fontPath)
//                        .addCustomViewWithSetTypeface(CustomViewWithTypefaceSupport.class)
//                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public Bus getBus() {
        if (mBus == null) {
            mBus = new Bus(ThreadEnforcer.ANY);
        }
        return mBus;
    }

    public DataSaver getDataSaver() {
        if (dataSaver == null) {
            dataSaver = new DataSaver(getApplicationContext(), "ArmutHV", false);
        }
        return dataSaver;
    }

    private Retrofit createRetrofitObject(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        if (event.getErrorMessage() == null) {
            if (KassaUtils.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Something went wrong, please try again" + event.getStatusCode(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "İnternetinizi kontrol edin ve tekrar deneyin.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (event.getStatusCode() == 401) {
                Toast.makeText(getApplicationContext(), "UnAuthorized - Lütfen giriş yapın.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), event.getStatusCode() + " - " + event.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

//    public void openOKDialog(final AppCompatActivity activity, final Fragment fragment) {
//        if (activity == null) {
//            return;
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                DialogNoConnection dialog = new DialogNoConnection();
//                Bundle args = new Bundle();
//                //if(dialogType.equals("no_connection")){
//                args.putString("title", getString(R.string.no_connection_title));
//                args.putString("message", getString(R.string.no_connection_with_button));
//                // }
//                String tag = "tag";
//                dialog.setArguments(args);
//                if (fragment != null) {
//                    //dialog.setTargetFragment(fragment, Constants.NO_CONNECTION);
//                    tag = fragment.getTag();
//                    try {
//                        dialog.show(fragment.getChildFragmentManager(), tag);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
////                else {
////                    if (activity instanceof MainActivity) {
////                        dialog.setTargetActivity((MainActivity) activity, Constants.NO_CONNECTION);
////                    }
////                    try{
////                        dialog.show(activity.getSupportFragmentManager(), tag);
////                    }catch(Exception e){
////                        e.printStackTrace();
////                    }
////                }
//            }
//        }, 25);
//    }

    public boolean requestCardWritePermission(Activity activity) {
        if (!KassaUtils.isMarshmallow()) {
            isWriteToSdCardPermissionGranted = true;
            return true;
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isWriteToSdCardPermissionGranted = false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessage(activity, "Mesaj'a fotoğraf ekleyebilmek için çekilen fotoğrafı SD kartınıza yüklememize izin verin.", Toast.LENGTH_LONG);
            }
//            else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//            }
        } else {
            isWriteToSdCardPermissionGranted = true;
        }
        return isWriteToSdCardPermissionGranted;
    }

    public void showMessage(final Activity activity, final String message, final int length) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, length).show();
            }
        });
    }
}
