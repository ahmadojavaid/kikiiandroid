package com.jobesk.kikiiapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobesk.kikiiapp.Adapters.PostMediaAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.Post;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SelectImage;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.ShowSelectImageBottomSheet;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobesk.kikiiapp.Utils.CommonMethods.goBack;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreatePostActivity";
    private Context context = CreatePostActivity.this;
    private EditText et_body;
    private TextView tv_name, tv_update;
    private ImageView img_select_image, img_back, img_ok;
    private CircleImageView img_user;
    private Call<CallbackStatus> callbackStatusCall;
    private CallbackStatus responseStatus;
    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private Map<String, String> createPostParams = new HashMap<>();
    private Map<String, String> updatePostParam = new HashMap<>();
    private Map<String, RequestBody> createPostMultipartParams = new HashMap<>();
    private Map<String, RequestBody> updatePostMultipartParams = new HashMap<>();
    private String body;
    private RecyclerView rv_media;
    private PostMediaAdapter postMediaAdapter;
    private List<String> newMediaPaths = new ArrayList<>();
    private List<String> previousMediaPaths = new ArrayList<>();
    private List<MultipartBody.Part> imagesList = new ArrayList<>();
    private Post post;
    private boolean isUpdatingPost = false;
    private HashSet<String> deletedMediaList = new HashSet<>();
    private ScrollView parent_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        parent_container = findViewById(R.id.parent_container);

        getIntentData();
        initComponents();
        setUserData();

        img_back.setOnClickListener(this);
        img_ok.setOnClickListener(this);
        img_select_image.setOnClickListener(this);
        tv_update.setOnClickListener(this);

    }

    private void getIntentData() {

        Intent intent = getIntent();
        if (intent.hasExtra("post")) {
            Bundle bundle = intent.getExtras();
            post = (Post) bundle.getSerializable("post");
            isUpdatingPost = true;
        }

    }

    private void setUserData() {

        if (isUpdatingPost)
            tv_update.setVisibility(View.VISIBLE);
        else
            img_ok.setVisibility(View.VISIBLE);

        tv_name.setText(sessionManager.getUserName());

        Glide.with(context)
                .load(sessionManager.getProfileUser().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_user);

        if (post != null) {
            setPostData();
        }

    }

    private void setPostData() {
        et_body.setText(post.getBody().replace("\"", ""));
        if (post.getMedia().size() > 0) {
            for (int i = 0; i < post.getMedia().size(); i++) {
                previousMediaPaths.add(post.getMedia().get(i).getPath());
            }
            postMediaAdapter.addAll(previousMediaPaths);
            rv_media.setAdapter(postMediaAdapter);
        }
    }

    private void initComponents() {

        sessionManager = new SessionManager(this);
        customLoader = new CustomLoader(this, false);

        et_body = findViewById(R.id.et_body);

        rv_media = findViewById(R.id.rv_media);

        tv_name = findViewById(R.id.tv_name);
        tv_update = findViewById(R.id.tv_update);

        img_user = findViewById(R.id.img_user);
        img_select_image = findViewById(R.id.img_select_image);
        img_back = findViewById(R.id.img_back);
        img_ok = findViewById(R.id.img_ok);

        if (isUpdatingPost)
            postMediaAdapter = new PostMediaAdapter(previousMediaPaths, context, "image", true);
        else
            postMediaAdapter = new PostMediaAdapter(newMediaPaths, context, "image", false);

        rv_media.setAdapter(postMediaAdapter);
        postMediaAdapter.setOnClickListeners(new PostMediaAdapter.IClicks() {
            @Override
            public void onCancelClick(View view, String path, int position) {
                Log.d(TAG, "onCancelClick: IMAGE_PICKER_REQUEST_CODE");
                for (int i = 0; i < post.getMedia().size(); i++) {
                    if (post.getMedia().get(i).getPath().equalsIgnoreCase(path)) {
                        Log.d(TAG, "onCancelClick: " + post.getMedia().get(i).getId());
                        deletedMediaList.add(post.getMedia().get(i).getId().toString());
                    }
                }
                postMediaAdapter.remove(path);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                goBack(this);
                break;
            case R.id.img_ok:
                if (et_body.getText().toString().trim().isEmpty()) {
                    et_body.setError(getResources().getString(R.string.et_error));
                } else {
                    body = et_body.getText().toString();
                    createPostParams.put(Constants.BODY, body);
                    if (newMediaPaths.size() > 0) {
                        RequestBody rBody = RequestBody.create(
                                MediaType.parse(Constants.BODY), body);
                        createPostMultipartParams.put(Constants.BODY, rBody);
                        uploadFiles();
                    } else {
                        createPost();
                    }
                }
                break;
            case R.id.img_select_image:
                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showDialogForSelectMedia();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
                break;
            case R.id.tv_update:
                if (et_body.getText().toString().trim().isEmpty()) {
                    et_body.setError(getResources().getString(R.string.et_error));
                } else {
                    String deletedMediaIds = null;
                    body = et_body.getText().toString();
                    updatePostParam.put(Constants.BODY, body);
                    if (newMediaPaths.size() > 0) {
                        if (post.getMedia().size() > 0) {
                            for (int i = 0; i < post.getMedia().size(); i++) {
                                deletedMediaList.add(post.getMedia().get(i).getId().toString());
                            }
                            deletedMediaIds = android.text.TextUtils.join(",", deletedMediaList);
                            deletedMediaIds = deletedMediaIds.replace("\"", "");
                            RequestBody rDeleteMediaIds = RequestBody.create(
                                    MediaType.parse(Constants.DELETED_MEDIA_IDS), deletedMediaIds);
                            updatePostMultipartParams.put(Constants.DELETED_MEDIA_IDS, rDeleteMediaIds);
                        }
                        RequestBody rBody = RequestBody.create(
                                MediaType.parse(Constants.BODY), body);
                        updatePostMultipartParams.put(Constants.BODY, rBody);
                        uploadFilesForUpdate();
                    } else {
                        if (deletedMediaList.size() > 0) {
                            deletedMediaIds = android.text.TextUtils.join(",", deletedMediaList);
                            deletedMediaIds = deletedMediaIds.replace("\"", "");
                            Log.d(TAG, "onClick: " + deletedMediaIds);
                            updatePostParam.put(Constants.DELETED_MEDIA_IDS, deletedMediaIds);
                        }
                        updatePost();
                    }
                }
                break;
        }
    }

    public void showDialogForSelectMedia() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreatePostActivity.this, R.style.AppBottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(CreatePostActivity.this)
                .inflate(R.layout.bottom_sheet_select_media_picker, parent_container, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetView.getBackground().setAlpha(0);
        } else {
            bottomSheetView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        }
        TextView tv_select_image = bottomSheetView.findViewById(R.id.tv_select_image);
//        TextView tv_select_video = bottomSheetView.findViewById(R.id.tv_select_video);

        tv_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ImagePicker.Builder(CreatePostActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(500, 500)
                        .allowMultipleImages(true)
                        .enableDebuggingMode(true)
                        .build();
                bottomSheetDialog.dismiss();

                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
//        tv_select_video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new VideoPicker.Builder(CreatePostActivity.this)
//                        .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
//                        .directory(VideoPicker.Directory.DEFAULT)
//                        .extension(VideoPicker.Extension.MP4)
//                        .enableDebuggingMode(true)
//                        .build();
//                bottomSheetDialog.dismiss();
//                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
//            }
//        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void uploadFiles() {
        for (int i = 0; i < newMediaPaths.size(); i++) {
            imagesList.add(SelectImage.prepareFilePart("media[]", newMediaPaths.get(i)));
        }
        createPostWithImages();
        /*File[] filesToUpload = new File[mediaPaths.size()];
        for (int i = 0; i < mediaPaths.size(); i++) {
            filesToUpload[i] = new File(mediaPaths.get(i));
        }
        customLoader.showIndicator();*/

        /*//showProgress("Uploading media ...");
        FileUploader fileUploader = new FileUploader(this);
        fileUploader.uploadFiles("media[]", filesToUpload, new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                customLoader.hideIndicator();
                Log.e("RESPONSE ","error");
            }

            @Override
            public void onFinish(String[] responses) {
                customLoader.setProgressVisibility(false);
                customLoader.hideIndicator();
                onBackPressed();
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
                customLoader.setProgressVisibility(true);
                customLoader.setProgress("Uploading \n"+totalpercent+"%");
                Log.e("Progress Status", currentpercent + " " + totalpercent + " " + filenumber);
            }
        },sessionManager.getAccessToken());*/
    }

    public void uploadFilesForUpdate() {
        for (int i = 0; i < newMediaPaths.size(); i++) {
            imagesList.add(SelectImage.prepareFilePart("new_media[]", newMediaPaths.get(i)));
        }
        updatePostWithImages();
        /*File[] filesToUpload = new File[mediaPaths.size()];
        for (int i = 0; i < mediaPaths.size(); i++) {
            filesToUpload[i] = new File(mediaPaths.get(i));
        }
        customLoader.showIndicator();*/

        /*//showProgress("Uploading media ...");
        FileUploader fileUploader = new FileUploader(this);
        fileUploader.uploadFiles("media[]", filesToUpload, new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                customLoader.hideIndicator();
                Log.e("RESPONSE ","error");
            }

            @Override
            public void onFinish(String[] responses) {
                customLoader.setProgressVisibility(false);
                customLoader.hideIndicator();
                onBackPressed();
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
                customLoader.setProgressVisibility(true);
                customLoader.setProgress("Uploading \n"+totalpercent+"%");
                Log.e("Progress Status", currentpercent + " " + totalpercent + " " + filenumber);
            }
        },sessionManager.getAccessToken());*/
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void createPost() {

        hideKeyboard(CreatePostActivity.this);

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "createPost: " + sessionManager.getAccessToken());
        callbackStatusCall = api.createPost(sessionManager.getAccessToken(), createPostParams);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    customLoader.hideIndicator();
                    if (responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        Intent intent = new Intent("refresh_list");
                        // You can also include some extra data.
                        intent.putExtra("message", "This is my message!");
                        LocalBroadcastManager.getInstance(CreatePostActivity.this).sendBroadcast(intent);


                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void updatePost() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "updatePost: " + sessionManager.getAccessToken());
        Log.d(TAG, "updatePost: " + updatePostParam.toString() + "  " + post.getId());
        callbackStatusCall = api.updatePost(sessionManager.getAccessToken(), post.getId().toString(), updatePostParam);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    customLoader.hideIndicator();
                    if (responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        Intent intent = new Intent("refresh_list");
                        // You can also include some extra data.
                        intent.putExtra("message", "This is my message!");
                        LocalBroadcastManager.getInstance(CreatePostActivity.this).sendBroadcast(intent);


                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void createPostWithImages() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "createPost: " + sessionManager.getAccessToken());
        callbackStatusCall = api.createPostWithMedia(sessionManager.getAccessToken(), createPostMultipartParams, imagesList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    customLoader.hideIndicator();
                    if (responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        Intent intent = new Intent("refresh_list");
                        // You can also include some extra data.
                        intent.putExtra("message", "This is my message!");
                        LocalBroadcastManager.getInstance(CreatePostActivity.this).sendBroadcast(intent);

                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void updatePostWithImages() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "createPost: " + sessionManager.getAccessToken());
        callbackStatusCall = api.updatePostWithNewMedia(sessionManager.getAccessToken(), post.getId().toString(), updatePostMultipartParams, imagesList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    customLoader.hideIndicator();
                    if (responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);

                        Intent intent = new Intent("refresh_list");
                        // You can also include some extra data.
                        intent.putExtra("message", "This is my message!");
                        LocalBroadcastManager.getInstance(CreatePostActivity.this).sendBroadcast(intent);

                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            newMediaPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            Log.d("hhhh", "onActivityResult: " + newMediaPaths.size());
            rv_media.setLayoutManager(new GridLayoutManager(context, 3));
            postMediaAdapter = new PostMediaAdapter(newMediaPaths, context, "video", false);
            rv_media.setAdapter(postMediaAdapter);
            postMediaAdapter.setOnClickListeners(new PostMediaAdapter.IClicks() {
                @Override
                public void onCancelClick(View view, String path, int position) {
                    Log.d(TAG, "onCancelClick: VIDEO_PICKER_REQUEST_CODE");
                    postMediaAdapter.remove(path);
                }
            });
        }

        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            newMediaPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d("hhhh", "onActivityResult: " + newMediaPaths.size());
            rv_media.setLayoutManager(new GridLayoutManager(context, 3));
            postMediaAdapter = new PostMediaAdapter(newMediaPaths, context, "image", false);
            rv_media.setAdapter(postMediaAdapter);
            postMediaAdapter.setOnClickListeners(new PostMediaAdapter.IClicks() {
                @Override
                public void onCancelClick(View view, String path, int position) {
                    Log.d(TAG, "onCancelClick: IMAGE_PICKER_REQUEST_CODE");
                    postMediaAdapter.remove(path);
                }
            });
        }
    }
}
