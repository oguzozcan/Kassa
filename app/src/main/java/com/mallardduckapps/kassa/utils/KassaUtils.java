package com.mallardduckapps.kassa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mallardduckapps.kassa.BuildConfig;
import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.objects.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 28/07/16.
 */
public class KassaUtils {

    private static String deviceName = "";
    private static String deviceOs = "";
    public static String clientInfo = "";
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"); // ^\w+\@\w+\.\w+$

    public static String getErrorMessage(Response response){
        String message = response.message();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            InputStream stream = response.errorBody().byteStream();//bytes()
            byte[] buffer = new byte[4096]; //8192
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            Log.i("ERROR!", " Error RESPONSE json: " + new String(output.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(output != null)
            message  =  new JSONObject(new String(output.toByteArray())).getString("error_description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if(output != null)
            message  =  new JSONObject(new String(output.toByteArray())).getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("ERROR_MESSAGE", "MESSAGE: " + message);
        return message;
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static boolean isMarshmallow() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static String getBasicJson(BasicNameValuePair... params) throws JSONException {
        JSONObject object = new JSONObject();
        for (BasicNameValuePair param : params) {
            object.put(param.getName(), param.getValue());
        }
        Log.d("JSON", "JSON: " + object.toString());
        return object.toString();
    }

    public static void setImage(ImageView imageView, byte[] imageArray, int defaultSampleSize) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = defaultSampleSize;
        boolean imageSet = false;
        while (!imageSet) {
            try {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, bitmapOptions));
                imageSet = true;
            } catch (OutOfMemoryError e) {
                bitmapOptions.inSampleSize *= 2;
            }
        }
    }

    public static int[] getScreenSize(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        int[] sizes = {width, height};
        Log.d("SCREEN_SIZE", "WIDTH: " + width);
        Log.d("SCREEN_SIZE", "HEIGHT: " + height);
        Log.d("SCREEN_SIZE", "Density: " + activity.getResources().getDisplayMetrics().density);
        return sizes;
    }

    public static float dpFromPx(int px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(int dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * Returns the consumer friendly device name
     */
    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion + " (" + release + ")";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void sendMail(String email, String recipient, String subject, String imagePath, Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); // .concat(recipient)
        emailIntent.setType("message/rfc822");
        //emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_TEXT, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (imagePath != null) {
            Uri pngUri = Uri.parse("file://" + imagePath);//Uri.parse(imagePath);
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, pngUri);
            //emailIntent.setType("application/image");
        }
        //i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
        // i.setType("image/png");
        try {
            activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_mail)));
            // finish();
            Log.i("Email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getString(R.string.no_mail_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void createClientInfo() throws JSONException {
        deviceName = getDeviceName();
        deviceOs = getAndroidVersion();
        clientInfo = getBasicJson(new BasicNameValuePair("client_os", "Android"), new BasicNameValuePair("client_os_version", deviceOs), new BasicNameValuePair("client", deviceName),
                new BasicNameValuePair("app_version_code", Integer.toString(BuildConfig.VERSION_CODE)),
                new BasicNameValuePair("app_version", BuildConfig.VERSION_NAME), new BasicNameValuePair("application_type", Integer.toString(1)));
        //0 stands for ha app, 1 for hv
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static int getActionBarSize(Activity activity){
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize =  ((int) styledAttributes.getDimension(0, 0));
        styledAttributes.recycle();
        return actionBarSize;
    }
}
