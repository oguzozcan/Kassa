package com.mallardduckapps.kassa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.mallardduckapps.kassa.busevents.AuthEvents;
import com.mallardduckapps.kassa.busevents.MiscEvents;
import com.mallardduckapps.kassa.fragments.DialogPhotoUpload;
import com.mallardduckapps.kassa.objects.Person;
import com.mallardduckapps.kassa.objects.Picture;
import com.mallardduckapps.kassa.objects.RegisterUser;
import com.mallardduckapps.kassa.objects.User;
import com.mallardduckapps.kassa.services.MiscServices;
import com.mallardduckapps.kassa.services.PhotoSelectorHelper;
import com.mallardduckapps.kassa.utils.Constants;
import com.mallardduckapps.kassa.utils.KassaUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, PhotoSelectorHelper.PhotoSelector {

    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mLastNameView;
    private View mProgressView;
    private View mLoginFormView;
    private String photoUrl = null;

    @Override
    protected void setTag() {
        TAG = "RegisterActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        mLastNameView = (AutoCompleteTextView) findViewById(R.id.surname) ;

        Button mEmailSignInButton = (Button) findViewById(R.id.register_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                attemptRegister();
            }
        });

        TextView loginTv = (TextView) findViewById(R.id.login);
        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
//        showProgress(true);
//        String accessToken = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
//        passToMainActivity(accessToken);
        getSupportActionBar().hide();

        RelativeLayout addImageLayout = (RelativeLayout) findViewById(R.id.addImageLayout);
        addImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAttachmentMenu();
            }
        });
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

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, Constants.MY_PERMISSIONS_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, Constants.MY_PERMISSIONS_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
//        if (mAuthTask != null) {
//            return;
//        }
        hideKeyboard();
        // Reset errors.
        mEmailView.setError(null);
        mNameView.setError(null);
        mLastNameView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String name = mNameView.getText().toString();
        String surname = mLastNameView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(surname)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
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
            Log.d(TAG, "SEND Register REQUEST email: " + email + " firstName: " + name + " - lastName: " + surname);
            Log.d(TAG, "TOKEN: " + app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY));
            RegisterUser registerUser = new RegisterUser();
            registerUser.setName(name);
            registerUser.setSurname(surname);
            registerUser.setEmail(email);
            if(photoUrl != null){
                registerUser.setPhotoUrl(photoUrl);
            }
            Gson gson = new Gson();
            Log.d(TAG, "JSON : " +gson.toJson(registerUser));
            app.getBus().post(new AuthEvents.RegisterRequest(registerUser));
        }
    }

    private boolean isEmailValid(String email) {
        return KassaUtils.EMAIL_PATTERN.matcher(email).matches();//email.contains("@") && email.trim().length() > 4;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void passToMainActivity(String accessToken) {
        if (!accessToken.equals("")) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            BaseActivity.setTranslateAnimation(RegisterActivity.this);
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

//    @Subscribe
//    public void getAuthStatus(AuthEvents.AuthResponse object) {
//        showProgress(false);
//
//        if (object.getResponse() != null) {
//            Log.d(TAG, "AUTH RESPONSE: " + object.getResponse().body().getAccessToken());//toString());
//            String accessToken = object.getResponse().body().getAccessToken();
//            if (accessToken != null) {
//                app.getDataSaver().putString(Constants.ACCESS_TOKEN_KEY, "bearer " + accessToken);
//                //app.dataSaver.putString(Constants.USERNAME, userName);
//                app.getDataSaver().save();
//                setResult(Activity.RESULT_OK);
//                passToMainActivity(accessToken);
//            }
//        }//Auth Failed
//        else {
//            Log.d(TAG, "AUTH RESPONSE: " +object.getMessage());//toString());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                    //  mPasswordView.requestFocus();
//                }
//            });
//        }
//    }

    @Subscribe
    public void getRegisteredUser(AuthEvents.RegisterResponse response) {
        showProgress(false);
        if (response != null) {
            if (response.getUser() != null) {
                User user = response.getUser().body();
                //TODO what to do with user
                if (user != null) {
                    String accessToken = app.getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
                    app.saveUser(user);
                    passToMainActivity(accessToken);
//                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            }
        }
    }

    @Subscribe
    public void onImageUploaded(MiscEvents.PostProfilePicResponse response){
        Log.d(TAG, "ON IMAGE UPLOADED");
        if(response != null){
            Person person = response.getPerson().body();
            String postFix = person.getPhotoUrl();
            if(postFix != null){
                //TODO show image
                showImage(postFix);
            }
        }
    }

    private void showImage(String imageSource) {
        if (imageSource == null) {
            return;
        }
        try {
            ImageView profileThumbnail = (ImageView)findViewById(R.id.profileThumbnail);
            ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
            switcher.setDisplayedChild(1);
            String source = Constants.PHOTO_BASE_URL + imageSource;
            Picasso.with(getApplicationContext()).load(source).into(profileThumbnail); // .placeholder(R.drawable.default_users_7)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAttachmentMenu() {
        Log.d(TAG, "OPEN ATTACHMENT MENU");
        if (app.requestCardWritePermission(this, getString(R.string.warning_photo_upload_profile))) {
            final DialogPhotoUpload uploadDialog = DialogPhotoUpload.newInstance();
            //uploadDialog.setTargetFragment(UserProfileFragment.this, Constants.UPLOAD_PHOTO_DIALOG);
            uploadDialog.show(getSupportFragmentManager(), "uploadDialog");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    app.isWriteToSdCardPermissionGranted = true;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // EXECUTE ACTIONS (LIKE FRAGMENT TRANSACTION ETC.)
                            final DialogPhotoUpload uploadDialog = DialogPhotoUpload.newInstance();
                            //uploadDialog.setTargetFragment(UserProfileFragment.this, Constants.UPLOAD_PHOTO_DIALOG);
                            uploadDialog.show(getSupportFragmentManager(), "uploadDialog");
                        }
                    }, 10);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    app.isWriteToSdCardPermissionGranted = false;
                }
                break;
            }
            case Constants.MY_PERMISSIONS_READ_CONTACTS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateAutoComplete();
                }
            }
        }
    }

    private PhotoSelectorHelper helper;

    @Override
    public void uploadMethodSelected(int status) {
        Log.d(TAG, "UPLOAD METHOD SELECTED : " + status);
        //if (attached && getActivity() != null) {
        Log.d(TAG, "HELPER CREATED for photo upload");
        helper = new PhotoSelectorHelper(this);
        switch (status) {
            case PhotoSelectorHelper.REQ_CAMERA:
                helper.takePicture();
                break;
            case PhotoSelectorHelper.REQ_PICK_IMAGE:
                helper.openGallery();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "REQUEST CODE: " + requestCode);
        Log.d(TAG, "RESULT CODE: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            //TODO handle error
            return;
        }
        switch (requestCode) {
            case PhotoSelectorHelper.REQ_PICK_IMAGE:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(helper.createImageFile());
                    PhotoSelectorHelper.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case PhotoSelectorHelper.REQ_CAMERA:
                break;
        }

        if (requestCode == PhotoSelectorHelper.REQ_CAMERA || requestCode == PhotoSelectorHelper.REQ_PICK_IMAGE) {
            Log.d(TAG, "SEND PHOTO - REQUEST CODE : " + requestCode);
            if (helper != null) {
                byte[] imageEncoded = helper.initPhotoForUpload();
                String encodedImage = Base64.encodeToString(imageEncoded, Base64.DEFAULT);
                Log.d(TAG, "ENCODED IMAGE: " + encodedImage);
                photoUrl = encodedImage;
                Bitmap bmp = BitmapFactory.decodeByteArray(imageEncoded, 0, imageEncoded.length);
                CircleImageView image = (CircleImageView) findViewById(R.id.profileThumbnail);
                image.setVisibility(View.VISIBLE);
                ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
                switcher.setDisplayedChild(1);
                image.setImageBitmap(bmp);

                //TODO
               // app.getBus().post(new MiscEvents.PostProfilePicRequest(new Picture(encodedImage, "jpeg")));
                //sendMessage(null, Message.MESSAGE_TYPE_IMAGE, encodedImage);
//                Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : START PHOTO UPLOAD TASK");
//                //loadingLayout.setVisibility(View.VISIBLE);
            }
            return;
        }
        Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
//        if (userUpdatePart != Constants.UPDATE_USER_PROFILE_PAGE) {
//            useLoader();
//        } else {
//            fillUserInfo(rootView, getActivity().getAssets());
//            //addCreditCardData();
//            //updateProfileInfo(me.getUserInfo());
//            switcher.setDisplayedChild(1);
//        }
    }

}


