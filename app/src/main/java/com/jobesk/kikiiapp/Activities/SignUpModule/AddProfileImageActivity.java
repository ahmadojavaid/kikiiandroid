package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.jobesk.kikiiapp.Utils.ShowSelectImageBottomSheet;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProfileImageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddProfileImageActivity";
    private Activity activity = AddProfileImageActivity.this;
    private static final int TAKE_PICTURE_FROM_CAMERA_FOR_PROFILE = 100;
    private static final int TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE = 200;
    private Button btn_add_image;
    private boolean isSelected;
    private SessionManager sessionManager;
    private Bitmap bitmap;
    private String currentPhotoPath;
    private CustomLoader customLoader;
    private Call<CallbackUpdateProfile> callbackStatusCall;
    private CallbackUpdateProfile responseUpdatePhoto;
    private ImageView img_back;
    private List<String> mediaPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_image);

        initComponents();

        btn_add_image.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private void initComponents() {
        btn_add_image = findViewById(R.id.btn_add_image);

        sessionManager = new SessionManager(this);

        customLoader = new CustomLoader(this, false);

        img_back = findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_image:
                ShowSelectImageBottomSheet.showDialog(activity, getWindow().getDecorView().getRootView(), Constants.SINGLE);
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        customLoader.showIndicator();
        /**PROFILE PICTURE FROM CAMERA**/
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d("hhhh", "onActivityResult: " + mediaPaths.size());
            currentPhotoPath = mediaPaths.get(0);
            MultipartBody.Part body = SelectImage.prepareFilePart(Constants.PROFILE_PIC, currentPhotoPath);
            RequestBody token = RequestBody.create(MediaType.parse("Authorization"), sessionManager.getAccessToken());
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("Authorization", token);
            updateProfilePhoto(body);
            /*if (requestCode == TAKE_PICTURE_FROM_CAMERA_FOR_PROFILE && resultCode == Activity.RESULT_OK) {
             *//* isSelected = true;
            bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = SelectImage.getImageUri(activity, bitmap);
            if (uri != null) {
                currentPhotoPath=SelectImage.compressImage(uri,activity);
                Log.d(TAG, "onActivityResult: " + currentPhotoPath);
                MultipartBody.Part body = SelectImage.prepareFilePart(Constant.PROFILE_PIC,currentPhotoPath);
                RequestBody token = RequestBody.create(MediaType.parse("Authorization"), sessionManager.getAccessToken());
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("Authorization", token);
                updateProfilePhoto(body);
            } else {
                customLoader.hideIndicator();
                isSelected = false;
                Toast.makeText(activity, "Image not captured", Toast.LENGTH_SHORT).show();
            }*//*
            mediaPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d("hhhh", "onActivityResult: "+ mediaPaths.size());
            MultipartBody.Part body = SelectImage.prepareFilePart(Constant.PROFILE_PIC,mediaPaths.get(0));
            RequestBody token = RequestBody.create(MediaType.parse("Authorization"), sessionManager.getAccessToken());
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("Authorization", token);
            updateProfilePhoto(body);

        }
        else if (requestCode == TAKE_PICTURE_FROM_GALLERY_FOR_PROFILE && resultCode == Activity.RESULT_OK) {
           *//* isSelected = true;
            bitmap = null;
            Uri pictureUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), pictureUri);
                isSelected = true;
                currentPhotoPath=SelectImage.compressImage(pictureUri,activity);
                Log.d(TAG, "onActivityResult: " + currentPhotoPath);
                MultipartBody.Part body = prepareFilePart(Constant.PROFILE_PIC,currentPhotoPath);
                RequestBody token = RequestBody.create(MediaType.parse("Authorization"), sessionManager.getAccessToken());
                HashMap<String, RequestBody> map = new HashMap<>();
                map.put("Authorization", token);
                updateProfilePhoto(body);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }*//*
            mediaPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d("hhhh", "onActivityResult: "+ mediaPaths.size());
            MultipartBody.Part body = SelectImage.prepareFilePart(Constant.PROFILE_PIC,mediaPaths.get(0));
            RequestBody token = RequestBody.create(MediaType.parse("Authorization"), sessionManager.getAccessToken());
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("Authorization", token);
            updateProfilePhoto(body);
        }*/
        } else {
            customLoader.hideIndicator();
        }
    }

    public MultipartBody.Part prepareFilePart(String partName, String currentPhotoPath) {
        File file = new File(currentPhotoPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse(partName), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void updateProfilePhoto(MultipartBody.Part body) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "updateProfilePhoto: " + sessionManager.getAccessToken());
        callbackStatusCall = api.updateProfilePhoto(sessionManager.getAccessToken(), body);
        callbackStatusCall.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d("ressss", "onResponse: " + response);
                responseUpdatePhoto = response.body();
                if (responseUpdatePhoto != null) {
                    if (responseUpdatePhoto.getSuccess()) {
                        Toast.makeText(activity, responseUpdatePhoto.getMessage(), Toast.LENGTH_SHORT).show();
                        customLoader.hideIndicator();
                        goToNextActivity(currentPhotoPath);
                    } else {
                        Toast.makeText(activity, responseUpdatePhoto.getMessage(), Toast.LENGTH_SHORT).show();
                        customLoader.hideIndicator();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d("ressss", "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void goToNextActivity(String path) {
        Intent intent = new Intent(activity, VerifyProfileImagesActivity.class);
        intent.putExtra("bitmap", path);
        startActivity(intent);
    }
}
