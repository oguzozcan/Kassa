package com.mallardduckapps.kassa.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.mallardduckapps.kassa.R;
import com.mallardduckapps.kassa.services.PhotoSelectorHelper;

/**
 * Created by oguzemreozcan on 07/09/16.
 */
public class DialogPhotoUpload extends DialogFragment {

    private PhotoSelectorHelper.PhotoSelector callback;

    public DialogPhotoUpload() {
    }

    public static DialogPhotoUpload newInstance() {
        return new DialogPhotoUpload();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (PhotoSelectorHelper.PhotoSelector) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement PhotoSelector interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_photo_upload_method, container);
        getDialog().setCancelable(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        TextView dialogTitle = (TextView) rootView.findViewById(R.id.dialogTitle);
//        TextView dialogSubTitle = (TextView) rootView.findViewById(R.id.dialogSubtitle);
        getActivity().setFinishOnTouchOutside(true);
        Button logout = (Button) rootView.findViewById(R.id.shootPhotoButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.uploadMethodSelected(PhotoSelectorHelper.REQ_CAMERA);
                    dismiss();
                }
            }
        });
        Button cancelButton = (Button) rootView.findViewById(R.id.galleryButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.uploadMethodSelected(PhotoSelectorHelper.REQ_PICK_IMAGE);
                    dismiss();
                }
            }
        });

        return rootView;
    }
}


