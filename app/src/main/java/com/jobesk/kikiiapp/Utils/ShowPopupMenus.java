package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.jobesk.kikiiapp.Activities.CreatePostActivity;
import com.jobesk.kikiiapp.Activities.PostDetailActivity;
import com.jobesk.kikiiapp.Adapters.CommentsAdapter;
import com.jobesk.kikiiapp.Adapters.CommunityPostsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Fragments.Others.CommunityFragment;
import com.jobesk.kikiiapp.Model.Post;
import com.jobesk.kikiiapp.Model.PostComment;
import com.jobesk.kikiiapp.Model.SinglePost;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowPopupMenus {
    private static final String TAG = "ReplyOfCommentsBottom";

    private static CustomLoader customLoader;
    private static SessionManager sessionManager;

    private static Call<CallbackStatus> callbackDeletePost, callbackDeleteComment;
    private static CallbackStatus responseDeletePost, responseDeleteComment;

    /***FOR POSTS LIST***/
    public static void showPostMenu(final Activity activity, View view, final Post post, final int position, final RecyclerView rv_comments, final List<Post> communityPostsList, final CommunityPostsAdapter communityPostsAdapter) {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(activity);
        PopupMenu popup = new PopupMenu(activity, view);
        if (post.getUser().getId().toString().equalsIgnoreCase(sessionManager.getUserID())) {
            popup.getMenuInflater()
                    .inflate(R.menu.my_post_menu, popup.getMenu());
        } else {
            popup.getMenuInflater()
                    .inflate(R.menu.other_user_post_menu, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deletePost(activity, post.getId(), position, rv_comments, communityPostsList, communityPostsAdapter);
                        break;
                    case R.id.menu_update:
                        Intent intent = new Intent(activity, CreatePostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("post", post);
                        intent.putExtras(bundle);
                        activity.startActivityForResult(intent, CommunityFragment.REQUEST_UPDATE_POST);
                        break;
                    case R.id.menu_report:
                        reportPost(activity, post.getId());
                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    private static void deletePost(final Activity activity, Integer id, final int position, final RecyclerView rv_comments, final List<Post> communityPostsList, final CommunityPostsAdapter communityPostsAdapter) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "deleteComment: " + sessionManager.getAccessToken());
        callbackDeletePost = api.deletePost(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeletePost.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeletePost = response.body();
                if (responseDeletePost != null) {
                    if (responseDeletePost.getSuccess()) {
                        customLoader.hideIndicator();
                        communityPostsAdapter.remove(communityPostsList.get(position));
                    } else {
                        Log.d(TAG, "onResponse: " + responseDeletePost.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
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

    private static void reportPost(final Activity activity, Integer id) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "reportPost: " + sessionManager.getAccessToken());
        Log.d(TAG, "reportPost: " + id);

        callbackDeletePost = api.reportPost(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeletePost.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeletePost = response.body();
                if (responseDeletePost != null) {
                    if (responseDeletePost.getSuccess()) {
                        customLoader.hideIndicator();
                        showAlertReport(activity);
//                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: " + responseDeletePost.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
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

    public static void showAlertReport(Activity activity) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_report_success, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                deleteDialog.dismiss();
            }
        };
        handler.postDelayed(runnable, 2000);

        deleteDialog.show();
    }


    private static void reportComment(final Activity activity, Integer id) {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "reportComment: " + sessionManager.getAccessToken());
        Log.d(TAG, "reportComment: " + id);

        callbackDeletePost = api.reportComment(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeletePost.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeletePost = response.body();
                if (responseDeletePost != null) {
                    if (responseDeletePost.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: " + responseDeletePost.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());

                }
            }
        });

    }

    /***FOR SINGLE POST***/
    public static void showPostMenu(final Activity activity, View view, final Post post, final EditText et_comment, final String comment) {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(activity);
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater()
                .inflate(R.menu.my_post_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deletePost(activity, post.getId());
                        break;
                    case R.id.menu_update:
                        PostDetailActivity.IS_UPDATING_COMMENT = false;
                        et_comment.setText(comment);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public static void showPostMenu(final Activity activity, View view, final SinglePost post, final EditText et_comment, final String comment) {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(activity);
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater()
                .inflate(R.menu.my_post_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deletePost(activity, post.getId());
                        break;
                    case R.id.menu_update:
                        PostDetailActivity.IS_UPDATING_COMMENT = false;
                        et_comment.setText(comment);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private static void deletePost(final Activity activity, Integer id) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "deleteComment: " + sessionManager.getAccessToken());
        callbackDeletePost = api.deletePost(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeletePost.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeletePost = response.body();
                if (responseDeletePost != null) {
                    if (responseDeletePost.getSuccess()) {
                        customLoader.hideIndicator();
                        activity.onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseDeletePost.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeletePost.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
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

    /***FOR POSTS LIST***/
    public static void showCommentMenu(final Activity activity, View view, final PostComment postComment, final int position, final RecyclerView rv_comments, final List<PostComment> commentsList, final CommentsAdapter commentsAdapter, final TextView tv_no) {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(activity);
        PopupMenu popup;
        if (postComment.getUserId().toString().equalsIgnoreCase(sessionManager.getUserID())) {
            popup = new PopupMenu(activity, view);
            popup.getMenuInflater()
                    .inflate(R.menu.comment_menu_3, popup.getMenu());
        } else {
            popup = new PopupMenu(activity, view);
            popup.getMenuInflater()
                    .inflate(R.menu.comment_menu_2, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteComment(activity, postComment.getId(), position, rv_comments, commentsList, commentsAdapter, tv_no);
                        break;
                    case R.id.menu_report:
                        reportComment(activity, postComment.getId());
                        break;
                    case R.id.menu_update:

                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private static void deleteComment(final Activity activity, Integer id, final int position, final RecyclerView rv_comments, final List<PostComment> commentsList, final CommentsAdapter commentsAdapter, final TextView tv_no) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "deleteComment: " + sessionManager.getAccessToken());
        callbackDeleteComment = api.deleteComment(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeleteComment.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeleteComment = response.body();
                if (responseDeleteComment != null) {
                    if (responseDeleteComment.getSuccess()) {
                        customLoader.hideIndicator();
                        commentsAdapter.remove(commentsList.get(position));
                        activity.setResult(Activity.RESULT_OK);
                        if (commentsList.size() == 0) {
                            rv_comments.setVisibility(View.GONE);
                            tv_no.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseDeleteComment.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeleteComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
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

}
