package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Adapters.RepliesAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackAddComment;
import com.jobesk.kikiiapp.Model.PostComment;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobesk.kikiiapp.R;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowReplyOfCommentsBottomSheet {

    private static final String TAG = "ReplyOfCommentsBottom";
    private static CustomLoader customLoader;
    private static SessionManager sessionManager;
    private static Map<String, String> addCommentParams = new HashMap<>();

    private static Call<CallbackAddComment> callbackAddCommentCall;
    private static CallbackAddComment responseAddComment;

    private static RepliesAdapter repliesAdapter;
    private static LinearLayoutManager layoutManager;

    public static void showDialog(final Activity activity, View view, final Object obj) {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(activity);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                activity, R.style.AppBottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(activity)
                .inflate(R.layout.bottom_reply_of_comment,
                        (RelativeLayout) view.findViewById(R.id.bottomSheetLayout), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetView.getBackground().setAlpha(0);
        } else {
            bottomSheetView.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        }
//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        CircleImageView img_user = bottomSheetView.findViewById(R.id.img_user);
        TextView tv_name = bottomSheetView.findViewById(R.id.tv_name);
//        TextView tv_no = bottomSheetView.findViewById(R.id.tv_no);
        TextView tv_comment = bottomSheetView.findViewById(R.id.tv_comment);
        RecyclerView rv_replies = bottomSheetView.findViewById(R.id.rv_replies);
        final EditText et_comment = bottomSheetView.findViewById(R.id.et_comment);
        ImageView img_add_media = bottomSheetView.findViewById(R.id.img_add_media);
        ImageView img_send = bottomSheetView.findViewById(R.id.img_send);

        /***SET COMMENT DATA****/
        final PostComment postComment = (PostComment) obj;
        tv_name.setText(postComment.getCommenter().getName());
        tv_comment.setText(postComment.getBody());
        Glide
                .with(activity)
                .load(postComment.getCommenter().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_user);
        /*******/

        /****SET REPLIES LIST*****/
        if (postComment.getReplies() != null) {
            if (postComment.getReplies().size() > 0) {
                repliesAdapter = new RepliesAdapter(activity);
                repliesAdapter.addAll(postComment.getReplies());
                rv_replies.setLayoutManager(new LinearLayoutManager(activity));
                rv_replies.setAdapter(repliesAdapter);
            }
        }
        /********/

        /***ADD REPLY***/
        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_comment.getText().toString().isEmpty()) {
                    et_comment.setError(activity.getResources().getString(R.string.et_error));
                } else {
                    String comment = et_comment.getText().toString();
                    addCommentParams.put(Constants.BODY, comment);
                    addCommentParams.put(Constants.COMMENT_ID, String.valueOf(postComment.getId()));
                    addReply(activity);
                }
            }
        });
        /******/

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
//        setupFullHeight(activity, bottomSheetDialog);
    }

    private static void addReply(final Activity activity) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackAddCommentCall = api.addComment(sessionManager.getAccessToken(), addCommentParams);
        callbackAddCommentCall.enqueue(new Callback<CallbackAddComment>() {
            @Override
            public void onResponse(Call<CallbackAddComment> call, Response<CallbackAddComment> response) {
                Log.d(TAG, "onResponse: " + response);
                responseAddComment = response.body();
                if (responseAddComment != null) {
                    if (responseAddComment.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseAddComment.getMessage(), Toast.LENGTH_SHORT).show();
                        activity.onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseAddComment.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseAddComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
                }
            }

            @Override
            public void onFailure(Call<CallbackAddComment> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private static void setupFullHeight(Activity activity, BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight(activity);
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private static int getWindowHeight(Activity activity) {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
