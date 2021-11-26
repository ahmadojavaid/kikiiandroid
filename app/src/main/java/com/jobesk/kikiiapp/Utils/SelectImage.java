package com.jobesk.kikiiapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.core.app.ActivityCompat;

import com.jobesk.kikiiapp.Netwrok.Constants;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SelectImage {

    private static final int TAKE_PICTURE_FROM_CAMERA_FOR_PROFILE = 100;
    private static final int TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE = 200;
    private static Activity activityMain;
    private String currentPhotoPath;

    public static void getPermissions(Activity activity,int i,String type) {
        activityMain = activity;
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (!hasPermissions(activityMain
                , PERMISSIONS)) {
            ActivityCompat.requestPermissions(activityMain, PERMISSIONS, PERMISSION_ALL);
        } else {
            selectImageDialog(i,type);
        }
    }
    public static void getPermissions(Activity activity,int i) {
        activityMain = activity;
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        if (!hasPermissions(activityMain
                , PERMISSIONS)) {
            ActivityCompat.requestPermissions(activityMain, PERMISSIONS, PERMISSION_ALL);
        } else {
            selectImageDialog(i);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void selectImageDialog(int i,String type) {
        if (i == 1) {
           /* Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (activityMain != null && takePictureIntent.resolveActivity(activityMain.getPackageManager()) != null) {
                activityMain.startActivityForResult(takePictureIntent, TAKE_PICTURE_FROM_CAMERA_FOR_PROFILE);
            }*/
            new ImagePicker.Builder(activityMain)
                    .mode(ImagePicker.Mode.CAMERA)
                    .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                    .directory(ImagePicker.Directory.DEFAULT)
                    .extension(ImagePicker.Extension.JPG)
                    .scale(600, 600)
                    .allowMultipleImages(false)
                    .enableDebuggingMode(true)
                    .build();
        } else if (i == 2) {
            if(type.equalsIgnoreCase(Constants.SINGLE)){
                /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                activityMain.startActivityForResult(photoPickerIntent, TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE);*/
                new ImagePicker.Builder(activityMain)
                        .mode(ImagePicker.Mode.GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build();
            }
            else {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityMain.startActivityForResult(Intent.createChooser(intent,"Select Picture"), TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE);*/
                new ImagePicker.Builder(activityMain)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(600, 600)
                        .allowMultipleImages(true)
                        .enableDebuggingMode(true)
                        .build();
            }
        }
    }

    private static void selectImageDialog(int i) {
        if (i == 1) {
            new ImagePicker.Builder(activityMain)
                    .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                    .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                    .directory(ImagePicker.Directory.DEFAULT)
                    .extension(ImagePicker.Extension.JPG)
                    .scale(600, 600)
                    .allowMultipleImages(true)
                    .enableDebuggingMode(true)
                    .build();
        }
        else{
            new VideoPicker.Builder(activityMain)
                    .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                    .directory(VideoPicker.Directory.DEFAULT)
                    .extension(VideoPicker.Extension.MP4)
                    .enableDebuggingMode(true)
                    .build();
        }
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String compressImage(Uri picUri,Activity activity) {
        try {
            final String[] currentPhotoPath = {FilePath.getPath(activity
                    , picUri)};
            ImageCompression imageCompression = new ImageCompression(activity);
            imageCompression.execute(currentPhotoPath[0]);
            imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                @Override
                public void processFinish(String imagePath) {
                    try {
                        currentPhotoPath[0] = imagePath;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return currentPhotoPath[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] getByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public static MultipartBody.Part prepareFilePart(String partName,String currentPhotoPath) {
        File file = new File(currentPhotoPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse(partName), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    public static Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}
