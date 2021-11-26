package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SelectImage;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import com.joooonho.SelectableRoundedImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoreProfileImagesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddMoreProfileImagesAct";
    private Context context = AddMoreProfileImagesActivity.this;
    private Activity activity = AddMoreProfileImagesActivity.this;

    private Button btn_next;
    private static final int TAKE_PICTURE_FROM_CAMERA_FOR_PROFILE = 100;
    private static final int TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE = 200;

    private boolean isSelected;
    private SessionManager sessionManager;
    private Bitmap bitmap, bmp;
    private String currentPhotoPath;
    private CustomLoader customLoader;

    private SelectableRoundedImageView img_user, img_selected_1, img_selected_2, img_selected_3, img_selected_4, img_selected_5, img_selected_6, img_selected_7, img_selected_8;
    private ImageView img_select_1, img_select_2, img_select_3, img_select_4, img_select_5, img_select_6, img_select_7, img_select_8;
    private static int setImageOn = 0;
    private MultipartBody.Part[] imageParts = new MultipartBody.Part[10];
    private ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
    private ArrayList<String> files = new ArrayList<>();
    private List<MultipartBody.Part> imagesList = new ArrayList<>();
    private ImageView img_back;
    private Uri imageUri;
    private String path;
    private List<String> mediaPaths = new ArrayList<>();

    private Call<CallbackUpdateProfile> callbackUpdateProfile;
    private CallbackUpdateProfile responseUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_profile_images);

        initComponents();
        getIntentData();

        btn_next.setOnClickListener(this);
        img_select_1.setOnClickListener(this);
        img_select_2.setOnClickListener(this);
        img_select_3.setOnClickListener(this);
        img_select_4.setOnClickListener(this);
        img_select_5.setOnClickListener(this);
        img_select_6.setOnClickListener(this);
        img_select_7.setOnClickListener(this);
        img_select_8.setOnClickListener(this);
        img_back.setOnClickListener(this);


    }

    private void getIntentData() {
      /*  Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("bitmap");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        img_user.setImageBitmap(bmp);*/
        Bundle extras = getIntent().getExtras();
       /* byte[] byteArray = extras.getByteArray("bitmap");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
        path = extras.getString("bitmap");
        imageUri = Uri.parse(path);
        img_user.setImageURI(imageUri);
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        btn_next = findViewById(R.id.btn_next);

        img_user = findViewById(R.id.img_user);
        img_selected_1 = findViewById(R.id.img_selected_1);
        img_selected_2 = findViewById(R.id.img_selected_2);
        img_selected_3 = findViewById(R.id.img_selected_3);
        img_selected_4 = findViewById(R.id.img_selected_4);
        img_selected_5 = findViewById(R.id.img_selected_5);
        img_selected_6 = findViewById(R.id.img_selected_6);
        img_selected_7 = findViewById(R.id.img_selected_7);
        img_selected_8 = findViewById(R.id.img_selected_8);

        img_select_1 = findViewById(R.id.img_select_1);
        img_select_2 = findViewById(R.id.img_select_2);
        img_select_3 = findViewById(R.id.img_select_3);
        img_select_4 = findViewById(R.id.img_select_4);
        img_select_5 = findViewById(R.id.img_select_5);
        img_select_6 = findViewById(R.id.img_select_6);
        img_select_7 = findViewById(R.id.img_select_7);
        img_select_8 = findViewById(R.id.img_select_8);

        img_back = findViewById(R.id.img_back);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                //callUploadData();
                if (mediaPaths.size() > 0 && mediaPaths.size() > 2) {
                    uploadFiles();
                } else {
                    Toast.makeText(context, "Please select at-least 3 images!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_select_1:

                setImageOn = 1;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);

                break;
            case R.id.img_select_2:
                setImageOn = 2;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_3:
                setImageOn = 3;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_4:
                setImageOn = 4;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_5:
                setImageOn = 5;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_6:
                setImageOn = 6;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_7:
                setImageOn = 7;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_select_8:
                setImageOn = 8;
                givePermission();
//                showDialog(activity, getWindow().getDecorView().getRootView(), Constants.MULTIPLE);
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }


    private void givePermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {

                    new ImagePicker.Builder(activity)
                            .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                            .directory(ImagePicker.Directory.DEFAULT)
                            .extension(ImagePicker.Extension.JPG)
                            .scale(500, 500)
                            .allowMultipleImages(false)
                            .enableDebuggingMode(true)
                            .build();

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

    private void callUploadData() {
        for (int i = 0; i < mArrayUri.size(); i++) {
            getImageFilePath(mArrayUri.get(i));
        }
        if (files.size() > 0) {
            uploadFiles();
        }
    }


    public static void showDialog(final Activity activity, View view, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                activity, R.style.AppBottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(activity)
                .inflate(R.layout.bottom_sheet_add_image,
                        (RelativeLayout) view.findViewById(R.id.bottomSheetLayout), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetView.getBackground().setAlpha(0);
        } else {
            bottomSheetView.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        }
        TextView tv_select_from_camera = bottomSheetView.findViewById(R.id.tv_select_from_camera);
        TextView tv_select_from_gallery = bottomSheetView.findViewById(R.id.tv_select_from_gallery);
        TextView tv_select_from_facebook = bottomSheetView.findViewById(R.id.tv_select_from_facebook);
        TextView tv_select_from_instagram = bottomSheetView.findViewById(R.id.tv_select_from_instagram);

        tv_select_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage.getPermissions(activity, 1, type);
                bottomSheetDialog.dismiss();
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        tv_select_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage.getPermissions(activity, 2, type);
                bottomSheetDialog.dismiss();
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        tv_select_from_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        tv_select_from_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> temp = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (setImageOn >= mediaPaths.size())
                mediaPaths.add(temp.get(0));
            else if (setImageOn < mediaPaths.size())
                mediaPaths.add(setImageOn, temp.get(0));
            setUriToImage(Uri.parse(temp.get(0)));
            Log.d("hhhh", "onActivityResult: " + mediaPaths.size());
        } else {
            customLoader.hideIndicator();
        }
    }

    private void setUriToImage(Uri uri) {
        switch (setImageOn) {
            case 1:
                img_selected_1.setImageURI(uri);
                img_select_1.setVisibility(View.GONE);
                break;
            case 2:
                img_selected_2.setImageURI(uri);
                img_select_2.setVisibility(View.GONE);

                break;
            case 3:
                img_selected_3.setImageURI(uri);
                img_select_3.setVisibility(View.GONE);

                break;
            case 4:
                img_selected_4.setImageURI(uri);
                img_select_4.setVisibility(View.GONE);
                break;
            case 5:
                img_selected_5.setImageURI(uri);
                img_select_5.setVisibility(View.GONE);

                break;
            case 6:
                img_selected_6.setImageURI(uri);
                img_select_6.setVisibility(View.GONE);
                break;
            case 7:
                img_selected_7.setImageURI(uri);
                img_select_7.setVisibility(View.GONE);
                break;
            case 8:
                img_selected_8.setImageURI(uri);
                img_select_8.setVisibility(View.GONE);
                break;
        }
    }

    private void uploadFiles() {
        for (int i = 0; i < mediaPaths.size(); i++) {
            imagesList.add(SelectImage.prepareFilePart("new_pics[]", mediaPaths.get(i)));
        }
        updateProfileWithImages();
    }

    private void updateProfileWithImages() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(this);
        Log.d(TAG, "createPost: " + sessionManager.getAccessToken());
        callbackUpdateProfile = api.updateOtherImages(sessionManager.getAccessToken(), imagesList);
        callbackUpdateProfile.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUpdateProfile = response.body();
                if (responseUpdateProfile != null) {
                    customLoader.hideIndicator();
                    if (responseUpdateProfile.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseUpdateProfile.getMessage());
                        Toast.makeText(context, responseUpdateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, AgreementActivity.class));
                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseUpdateProfile.getMessage());
                        Toast.makeText(context, responseUpdateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

   /* private void setBitmap(Bitmap bitmap) {
        switch (setImageOn) {
            case 1:
                img_selected_1.setImageBitmap(bitmap);
                img_select_1.setVisibility(View.GONE);
                break;
            case 2:
                img_selected_2.setImageBitmap(bitmap);
                img_select_2.setVisibility(View.GONE);

                break;
            case 3:
                img_selected_3.setImageBitmap(bitmap);
                img_select_3.setVisibility(View.GONE);

                break;
            case 4:
                img_selected_4.setImageBitmap(bitmap);
                img_select_4.setVisibility(View.GONE);
                break;
            case 5:
                img_selected_5.setImageBitmap(bitmap);
                img_select_5.setVisibility(View.GONE);

                break;
            case 6:
                img_selected_6.setImageBitmap(bitmap);
                img_select_6.setVisibility(View.GONE);
                break;
            case 7:
                img_selected_7.setImageBitmap(bitmap);
                img_select_7.setVisibility(View.GONE);
                break;
            case 8:
                img_selected_8.setImageBitmap(bitmap);
                img_select_8.setVisibility(View.GONE);
                break;
        }
    }*/

    private void setBitmap(Bitmap bitmap, int setImageOn) {
        switch (setImageOn) {
            case 1:
                img_selected_1.setImageBitmap(bitmap);
                img_select_1.setVisibility(View.GONE);
                break;
            case 2:
                img_selected_2.setImageBitmap(bitmap);
                img_select_2.setVisibility(View.GONE);

                break;
            case 3:
                img_selected_3.setImageBitmap(bitmap);
                img_select_3.setVisibility(View.GONE);

                break;
            case 4:
                img_selected_4.setImageBitmap(bitmap);
                img_select_4.setVisibility(View.GONE);
                break;
            case 5:
                img_selected_5.setImageBitmap(bitmap);
                img_select_5.setVisibility(View.GONE);

                break;
            case 6:
                img_selected_6.setImageBitmap(bitmap);
                img_select_6.setVisibility(View.GONE);
                break;
            case 7:
                img_selected_7.setImageBitmap(bitmap);
                img_select_7.setVisibility(View.GONE);
                break;
            case 8:
                img_selected_8.setImageBitmap(bitmap);
                img_select_8.setVisibility(View.GONE);
                break;
        }
    }

    public void getImageFilePath(Uri uri) {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            files.add(imagePath);
            cursor.close();
        }
    }

    /*public void uploadFiles() {
        File[] filesToUpload = new File[files.size()];
        for (int i = 0; i < files.size(); i++) {
            filesToUpload[i] = new File(files.get(i));
        }
        customLoader.showIndicator();
        //showProgress("Uploading media ...");
        FileUploader fileUploader = new FileUploader(activity);
        fileUploader.uploadFiles("new_pics[]", filesToUpload, new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                customLoader.hideIndicator();
                Log.e("RESPONSE ","error");
            }

            @Override
            public void onFinish(String[] responses) {
                customLoader.setProgressVisibility(false);
                customLoader.hideIndicator();
                startActivity(new Intent(mContext,AgreementActivity.class));
                //hideProgress();
                for (int i = 0; i < responses.length; i++) {
                    String str = responses[i];
                    Log.e("RESPONSE " + i, responses[i]);
                }
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
                customLoader.setProgressVisibility(true);
                customLoader.setProgress("Uploading \n"+totalpercent+"%");
                Log.e("Progress Status", currentpercent + " " + totalpercent + " " + filenumber);
            }
        },sessionManager.getAccessToken());
    }
*/
   /* public void updateProgress(int val, String title, String msg){
        pDialog.setTitle(title);
        pDialog.setMessage(msg);
        pDialog.setProgress(val);
    }

    public void showProgress(String str){
        try{
            pDialog.setCancelable(false);
            pDialog.setTitle("Please wait");
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMax(100); // Progress Dialog Max Value
            pDialog.setMessage(str);
            if (pDialog.isShowing())
                pDialog.dismiss();
            pDialog.show();
        }catch (Exception e){

        }
    }

    public void hideProgress(){
        try{
            if (pDialog.isShowing())
                pDialog.dismiss();
        }catch (Exception e){

        }
    }*/

}
