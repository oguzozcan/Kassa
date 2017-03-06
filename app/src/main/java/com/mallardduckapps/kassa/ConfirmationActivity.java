package com.mallardduckapps.kassa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mallardduckapps.kassa.busevents.AuthEvents;
import com.mallardduckapps.kassa.busevents.MiscEvents;
import com.mallardduckapps.kassa.objects.Session;
import com.mallardduckapps.kassa.utils.Constants;
import com.squareup.otto.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConfirmationActivity extends BaseActivity {

    private TextInputLayout mPhoneView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void setTag() {
        TAG = "ConfirmationActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        mPhoneView = (TextInputLayout) findViewById(R.id.phone);
        mPhoneView.setErrorEnabled(true);
        mPhoneView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.phone_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
//        showProgress(true);
        String accessToken = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        getSupportActionBar().hide();
        boolean isExistingUser = app.getDataSaver().getBoolean(Constants.EXISTING_USER);
        if(isExistingUser){
            passToMainActivity(accessToken);
        }

        app.getBus().post(new MiscEvents.GetWebPageRequest());
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


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        hideKeyboard();
        mPhoneView.setError(null);
        mPhoneView.setErrorEnabled(false);
        String phoneNumber = mPhoneView.getEditText().getText().toString();
        //String convertedPhoneNumber = phoneNumber.substring(1);//"00" +
        Log.d(TAG, "ATTEMPT LOGIN : " + phoneNumber);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(phoneNumber) && !isPhoneValid(phoneNumber)) {
            mPhoneView.setErrorEnabled(true);
            mPhoneView.setError(getString(R.string.error_invalid_password));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Log.d(TAG, "SEND REQUEST TO GET CONFIRMATION CODE "  + phoneNumber);
            //Todo confirmation code is static now
            app.getBus().post(new AuthEvents.GetConfirmationCodeRequest(phoneNumber));
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() == 10;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showKeyboard(EditText editText) {
        if (editText != null) {
            editText.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void passToMainActivity(String accessToken){
        if(!accessToken.equals("")){
            Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
            startActivity(intent);
            BaseActivity.setTranslateAnimation(ConfirmationActivity.this);
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Subscribe
    public void onConfirmationCodeReady(AuthEvents.GetConfirmationCodeResponse response){
        Log.d(TAG, "CONFIRMATION CODE RECEIVED send it to get sessionId : " + response.getPhoneNumber());
        if(response.getResponse() != null){
            app.getBus().post(new AuthEvents.PostConfirmationCodeRequest(response.getPhoneNumber(), "123456"));
        }
    }

    @Subscribe
    public void getAppSession(AuthEvents.PostConfirmationCodeResponse object) {

        Log.d(TAG, "SESSION ID RECEIVED");
        if (object.getResponse() != null) {
            Session session = object.getResponse().body();
            Log.d(TAG, "GET APP SESSION RESPONSE: " + session.getAppSession().toString() + " - phone: " + object.getPhoneNumber() +  " - isUserExists : " + session.isUserExists());//toString());
            String sessionId = session.getAppSession();
            app.getDataSaver().putString(Constants.SESSION_ID, sessionId);
            app.getDataSaver().save();
            app.getBus().post(new AuthEvents.AuthRequest(session.isUserExists(), object.getPhoneNumber(), sessionId, "password", Constants.CLIENT_ID));
        }//Auth Failed
        else {
            showProgress(false);
        }
    }

    @Subscribe
    public void getAuthStatus(AuthEvents.AuthResponse object) {


        if (object.getResponse() != null) {
            Log.d(TAG, "AUTH RESPONSE: " + object.getResponse().body().getAccessToken() + " - isExistingUser : " + object.isExistingUser());//toString());
            String accessToken = object.getResponse().body().getAccessToken();
            if (accessToken != null) {
                app.getDataSaver().putString(Constants.ACCESS_TOKEN_KEY, "bearer " + accessToken);
                app.getDataSaver().putString(Constants.PHONE_NUMBER, object.getPhoneNumber());
                app.getDataSaver().save();
                setResult(Activity.RESULT_OK);

                if(object.isExistingUser()){
                    app.getDataSaver().putBoolean(Constants.EXISTING_USER, true);
                    app.getDataSaver().save();
                    passToMainActivity(accessToken);
                }else{
                    Log.d(TAG, "OPENM REGISTER ACTIVITY");
                    Intent intent = new Intent(ConfirmationActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }//Auth Failed
        else {
            Log.d(TAG, "AUTH RESPONSE failed: " +object.getMessage());//toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    //mPasswordView.setError(getString(R.string.error_incorrect_password));
                    //  mPasswordView.requestFocus();
                }
            });
        }
    }

}
