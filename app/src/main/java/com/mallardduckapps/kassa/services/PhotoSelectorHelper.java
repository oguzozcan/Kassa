package com.mallardduckapps.kassa.services;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.mallardduckapps.kassa.objects.Picture;
import com.mallardduckapps.kassa.utils.KassaUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by oguzemreozcan on 06/09/16.
 */
public class PhotoSelectorHelper {

    private final Activity activity;
    //    private final BasicFragment fragment;
    public final static int REQ_PICK_IMAGE = 0;
    public final static int REQ_CAMERA = 1;
    public final static int ADD_LOCATION = 2;
    private final static String TAG = "Photo_Selector";
    private final int width;
    private final int height;
    private int degree;
    private Uri newPhotoUri;
    private final int PHOTO_QUALITY = 75;

//    private PhotoSelectorHelper(Activity activity){
////        if(mFileTemp == null){
////            String state = Environment.getExternalStorageState();
////            if (Environment.MEDIA_MOUNTED.equals(state)) {
////                mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
////            } else {
////                mFileTemp = new File(activity.getFilesDir(), TEMP_PHOTO_FILE_NAME);
////            }
////        }
//    }

    public PhotoSelectorHelper(Activity activity){
        this.activity = activity;
//        this.fragment = fragment;
        int[] size = KassaUtils.getScreenSize(activity);
        width = size[0] * 2 / 3;
        height = size[1] * 2 / 3;
        Log.d(TAG, "CALCULATED WIDTH : " + width + "-height: " + height);
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                timeStamp,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        //mCurrentPhotoPath = timeStamp + ".jpg";
//        mCurrentPhotoAbsPath = "file:" +image.getAbsolutePath(); // "file:" + +
        newPhotoUri = Uri.fromFile(image);
        Log.d(TAG, "CREATE IMAGE: NEW IMAGE URI: " + newPhotoUri.getPath());
        return image;
    }

    public byte[] initPhotoForUpload(){
        if(newPhotoUri == null){
            Log.d(TAG, "NEW PHOTO URI IS NULL");
            return null;
        }
        try{
            Log.d(TAG, "PHOTO URI: " + newPhotoUri.toString());
            degree = getRotationForImage(newPhotoUri.getPath());
            Log.d(TAG, "DEGREE : " + degree);
        }catch(Exception e){
            e.printStackTrace();
        }
        Bitmap bm = decodeSampledBitmapFromFile(newPhotoUri.getPath(), width, height);//BitmapFactory.decodeFile(mFileTemp.getPath());
        if(bm == null){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, baos); //bm is the bitmap object
        // byte[] byteArrayImage = ;
        //String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return baos.toByteArray();
    }

//    public Picture initJobPhotoForUpload(){
//        if(newPhotoUri == null){
//            Log.d(TAG, "NEW PHOTO URI IS NULL");
//            return null;
//        }
//        String imageId = UUID.randomUUID().toString();
//        try{
//            Log.d(TAG, "PHOTO URI: " + newPhotoUri.toString());
//            degree = getRotationForImage(newPhotoUri.getPath());
//            Log.d(TAG, "DEGREE : " + degree);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        Bitmap bm = decodeSampledBitmapFromFile(newPhotoUri.getPath(), width, height);//BitmapFactory.decodeFile(mFileTemp.getPath());    mFileTemp.getPath()
//        if(bm == null){
//            return null;
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, baos); //bm is the bitmap object
//
//        Picture imageInfo = new Picture();
////        imageInfo.setImageId(imageId);
//        imageInfo.setImageEncoded(baos.toByteArray());
////        imageInfo.setPath(newPhotoUri.getPath());
////        imageInfo.setBatchId(UUID.randomUUID().toString());
//
//        // byte[] byteArrayImage = ;
//        //String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
//        return imageInfo;
//    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void openGallery() {
        //fromGallery = true;
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);//Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
//        if(fragment == null){
        activity.startActivityForResult(photoPickerIntent, REQ_PICK_IMAGE);
//        }else{
//            if(fragment.isDetached()){
//                return;
//            }
//            fragment.startActivityForResult(photoPickerIntent, REQ_PICK_IMAGE);
//        }
    }

    public void takePicture() {
        //fromGallery = false;
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(TAG, "UPLOAD FILE PATH : " + photoFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null) {
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));//getImageUri(mFileTemp)
                intent.putExtra("return-data", true);
//                if (fragment == null) {
                activity.startActivityForResult(intent, REQ_CAMERA);
//                } else {
//                    fragment.startActivityForResult(intent, REQ_CAMERA);
//                }
            }
        }
    }

    private int getRotationForImage(String path) {
        int rotation = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            rotation = exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotation;
    }

    private static int exifOrientationToDegrees(int exifOrientation) {
        int rotate = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                rotate = 90;
                break;
        }
        return rotate;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            int h = height / inSampleSize;
            int w = width / inSampleSize;
            while(h >= 1920 || w >= 1080){
                inSampleSize *=2;
                w = w/2;
                h = h/2;
            }
            Log.d(TAG, "CALCULATE REAL SIZE: " + w + " height: " + h);
        }
        Log.d(TAG, "CALCULATE IN SAMPLE SIZE: " + inSampleSize + " - height: " + height + " - width: " + width + " - required width: " + reqWidth + " - requiredHeight: " + reqHeight);

        return inSampleSize;
    }

    //    public Bitmap decodeSampledBitmapFromByteArray(byte[] data,
//                                                          int reqWidth, int reqHeight) {
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
////	    BitmapFactory.decodeResource(res, resId, options);
//        BitmapFactory.decodeByteArray(data, 0, data.length, options);
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
//    }
//TODO there might be problem test it
    private Bitmap decodeSampledBitmapFromFile(String filePath,
                                               int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.d(TAG, "DEGREE: " + degree);
        return rotateBitmap(BitmapFactory.decodeFile(filePath, options), degree); // 90
    }

    //    private void calculateDegree(){
//        try{
//            Log.d(TAG, "PHOTO URI: " + newPhotoUri.toString());
//            String[] projection = { MediaStore.Images.Media.DATA };
//            CursorLoader loader = new CursorLoader(activity, newPhotoUri, projection, null, null, null); //newPhotoUri
//            Cursor cursor = loader.loadInBackground();
//            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
////         Rotation is stored in an EXIF tag, and this tag seems to return 0 for URIs.
////         Hence, we retrieve it using an absolute path instead!
//            //int rotation = 0;
//            String realPath = cursor.getString(column_index_data);
//            Log.d(TAG, "REAL PATH: " + realPath);
//            //Log.d(TAG, "ABSOLUTE PATH : " + mCurrentPhotoAbsPath );
//            if (realPath != null) {
//                degree = getRotationForImage(realPath);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

//    public static File getJobImageFile(Activity activity, String path){
//        File file;
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            file = new File(Environment.getExternalStorageDirectory(), path);//TEMP_PHOTO_FILE_NAME
//        } else {
//            file = new File(activity.getFilesDir(), path); //TEMP_PHOTO_FILE_NAME
//        }
//        return file;
//    }

//    public static Uri getImageUri(File file){
//        Uri mImageCaptureUri;
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            mImageCaptureUri = Uri.fromFile(file);
//        } else {
//	        	/*
//	        	 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
//	        	 */
//            mImageCaptureUri = CONTENT_URI;
//        }
//
//        return mImageCaptureUri;
//    }

    public interface PhotoSelector{
        void uploadMethodSelected(int status);
    }
}
