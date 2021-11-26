package com.jobesk.kikiiapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Adapters.CommentsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackAddComment;
import com.jobesk.kikiiapp.Callbacks.CallbackGetPostComments;
import com.jobesk.kikiiapp.Callbacks.CallbackSinglePost;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.PostComment;
import com.jobesk.kikiiapp.Model.SinglePost;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.PaginationScrollListener;
import com.jobesk.kikiiapp.Utils.SelectImage;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.ShowPopupMenus;
import com.jobesk.kikiiapp.Utils.ShowSelectImageBottomSheet;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SinglePostDetailActivity extends AppCompatActivity implements View.OnClickListener, CommentsAdapter.IClicks {

    private static final String TAG = "PostDetailActivity";
    private Context context = SinglePostDetailActivity.this;
    private Activity activity = SinglePostDetailActivity.this;

    private RecyclerView rv_comments;
    private CommentsAdapter commentsAdapter;
    private LinearLayoutManager layoutManager;
    private List<PostComment> commentsList = new ArrayList<>();
    private SinglePost post;
    private String postId;
    private TextView tv_name, tv_description, tv_no, tv_time_ago, tv_update_comment;
    private CircleImageView img_user;
    private ImageView img_back, img_send, img_post_menu, img_select_media, img_selected, img_cancel;

    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private Map<String, String> addCommentParams = new HashMap<>();
    private Map<String, String> updateCommentParams = new HashMap<>();

    private Call<CallbackGetPostComments> callbackGetPostCommentsCall;
    private CallbackGetPostComments responsePostComments;

    private Call<CallbackSinglePost> callbackGetSinglePost;
    private CallbackSinglePost responseSinglePost;

    private Call<CallbackStatus> callbackDeleteComment;
    private CallbackStatus responseDeleteComment;

    private Call<CallbackAddComment> callbackAddCommentCall;
    private CallbackAddComment responseAddComment;

    private EditText et_comment;
    private String comment;
    public static boolean IS_UPDATING_COMMENT = false;
    private List<String> mediaPaths = new ArrayList<>();
    private RelativeLayout rl_media;
    private MultipartBody.Part media;
    private ImageView img_media;

    /*****/
    ProgressBar progressBar;

    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 20;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private int TOTAL_PAGES = 3;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;
    private String commentIdUpdating = "";

    /*****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initComponents();
        getPostData();

        img_send.setOnClickListener(this);
        img_post_menu.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_select_media.setOnClickListener(this);
        img_cancel.setOnClickListener(this);
        tv_update_comment.setOnClickListener(this);

        rv_comments.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
                Log.d(TAG, "loadMoreItems: hereee");
                if (currentPage != -1)
                    loadComments();

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void setPostData() {
        tv_name.setText(post.getUser().getName());
        tv_description.setText(post.getBody().replace("\"", ""));
        tv_time_ago.setText(post.getCreatedAt());
        Glide
                .with(context)
                .load(post.getUser().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_user);
        if (post.getMedia().size() > 0) {
            img_media.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(post.getMedia().get(0).getPath())
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_place_holder_image)
                    .into(img_media);

        }
        if (post.getComments().size() > 0) {
            tv_no.setVisibility(View.GONE);
            rv_comments.setVisibility(View.VISIBLE);
            setData();
        } else {
            customLoader.hideIndicator();
            tv_no.setVisibility(View.VISIBLE);
            rv_comments.setVisibility(View.GONE);
        }
    }

    private void getPostData() {
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.ID);
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetSinglePost = api.getSinglePost(sessionManager.getAccessToken(), postId);
        callbackGetSinglePost.enqueue(new Callback<CallbackSinglePost>() {
            @Override
            public void onResponse(Call<CallbackSinglePost> call, Response<CallbackSinglePost> response) {
                Log.d(TAG, "onResponse: " + response);
                responseSinglePost = response.body();
                if (responseSinglePost != null) {
                    if (responseSinglePost.getSuccess()) {
                        customLoader.hideIndicator();
                        post = responseSinglePost.getPost();
                        setPostData();
                        /*else
                            currentPage = -1;*/
                    } else {
                        Log.d(TAG, "onResponse: " + responseSinglePost.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseSinglePost.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackSinglePost> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void loadComments() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetPostCommentsCall = api.getSinglePostComments(sessionManager.getAccessToken(), String.valueOf(post.getId()), String.valueOf(currentPage));
        callbackGetPostCommentsCall.enqueue(new Callback<CallbackGetPostComments>() {
            @Override
            public void onResponse(Call<CallbackGetPostComments> call, Response<CallbackGetPostComments> response) {
                Log.d(TAG, "onResponse: " + response);
                responsePostComments = response.body();
                if (responsePostComments != null) {
                    if (responsePostComments.getSuccess()) {
                        customLoader.hideIndicator();
                        if (responsePostComments.getComments().size() > 0) {
                            if (currentPage == 0) {
                                tv_no.setVisibility(View.GONE);
                                rv_comments.setVisibility(View.VISIBLE);
                                setData();
                            } else {
                                currentPage = responsePostComments.getNextOffset();
                                if (responsePostComments.getComments().size() > 0) {
                                    commentsAdapter.addList(responsePostComments.getComments());
                                } else {
                                    isLastPage = true;
                                    currentPage = 0;
                                }
                                isLoading = false;
                                customLoader.hideIndicator();
                            }
                        } else {
                            if (currentPage != 0) {
                                isLastPage = true;
                                currentPage = 0;
                            } else {
                                tv_no.setVisibility(View.VISIBLE);
                                rv_comments.setVisibility(View.GONE);
                            }
                            customLoader.hideIndicator();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responsePostComments.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responsePostComments.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetPostComments> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        if (responsePostComments != null) {
            currentPage = responsePostComments.getNextOffset();
            if (responsePostComments.getComments() != null) {
                commentsList = responsePostComments.getComments();
                commentsAdapter.addAll(commentsList);
                rv_comments.setAdapter(commentsAdapter);

//                commentsAdapter.setOnClickListeners(new CommentsAdapter.IClicks() {
//                    @Override
//                    public void onMenuClick(View view, PostComment postComment, final int position) {
//                        showCommentMenu(activity, view, postComment, position, rv_comments, commentsList, commentsAdapter, tv_no);
//                    }
//                });
            }
        } else {
            currentPage = PAGE_START;
            commentsList = post.getComments();
            commentsAdapter.addAll(commentsList);
            rv_comments.setAdapter(commentsAdapter);

//            commentsAdapter.setOnClickListeners(new CommentsAdapter.IClicks() {
//                @Override
//                public void onMenuClick(View view, PostComment postComment, final int position) {
//                    showCommentMenu(activity, view, postComment, position, rv_comments, commentsList, commentsAdapter, tv_no);
//                }
//            });
        }
    }


    @Override
    public void onMenuClick(View view, PostComment data, int position) {
        showCommentMenu(activity, view, data, position, rv_comments, commentsList, commentsAdapter, tv_no);
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);
        commentsAdapter = new CommentsAdapter(activity);
        commentsAdapter.addAll(commentsList);

        rv_comments = findViewById(R.id.rv_comments);
        layoutManager = new LinearLayoutManager(context);
        rv_comments.setLayoutManager(layoutManager);
        rv_comments.setAdapter(commentsAdapter);

        tv_name = findViewById(R.id.tv_name);
        tv_description = findViewById(R.id.tv_description);
        tv_no = findViewById(R.id.tv_no);
        tv_time_ago = findViewById(R.id.tv_time_ago);
        tv_update_comment = findViewById(R.id.tv_update_comment);

        img_user = findViewById(R.id.img_user);
        img_back = findViewById(R.id.img_back);
        img_send = findViewById(R.id.img_send);
        img_post_menu = findViewById(R.id.img_post_menu);
        img_select_media = findViewById(R.id.img_select_media);
        img_selected = findViewById(R.id.img_selected);
        img_cancel = findViewById(R.id.img_cancel);
        img_media = findViewById(R.id.img_media);

        rl_media = findViewById(R.id.rl_media);

        et_comment = findViewById(R.id.et_comment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_send:
                if (IS_UPDATING_COMMENT) {
                    if (et_comment.getText().toString().isEmpty()) {
                        et_comment.setError(getResources().getString(R.string.et_error));
                    } else {
                        comment = et_comment.getText().toString();
                        addCommentParams.put(Constants.BODY, comment);
                        addCommentParams.put(Constants.POST_ID, String.valueOf(post.getId()));
                        addComment();
                    }

                } else {
                    if (et_comment.getText().toString().isEmpty()) {
                        et_comment.setError(getResources().getString(R.string.et_error));
                    } else {
                        if (mediaPaths.size() > 0) {
                            media = SelectImage.prepareFilePart(Constants.MEDIA, mediaPaths.get(0));
                            addCommentWithMedia();
                        } else {
                            comment = et_comment.getText().toString();
                            addCommentParams.put(Constants.BODY, comment);
                            addCommentParams.put(Constants.POST_ID, String.valueOf(post.getId()));
                            addComment();
                        }
                    }
                }
                break;
            case R.id.tv_update_comment:
                if (et_comment.getText().toString().isEmpty()) {
                    et_comment.setError(getResources().getString(R.string.et_error));
                } else {
                    comment = et_comment.getText().toString();
                    updateCommentParams.put(Constants.BODY, comment);
                    updateCommentParams.put(Constants.POST_ID, String.valueOf(post.getId()));
                    updateComment();
                }
                break;
            case R.id.img_post_menu:
                ShowPopupMenus.showPostMenu(activity, img_post_menu, post, et_comment, comment);
                break;
            case R.id.img_back:
                onBackPressed();
                //CommonMethods.goBack(this);
                break;
            case R.id.img_select_media:
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_media, Constants.SINGLE);
                break;
            case R.id.img_cancel:
                rl_media.setVisibility(View.GONE);
                img_selected.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_dummy));
                mediaPaths.clear();
                break;
        }
    }

    private void addComment() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
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
                        commentsAdapter.add(responseAddComment.getComment());
                        et_comment.setText("");
                        tv_no.setVisibility(View.GONE);
                        rv_comments.setVisibility(View.VISIBLE);
                        setResult(RESULT_OK);
                        View view = SinglePostDetailActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseAddComment.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAddComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    private void updateComment() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackAddCommentCall = api.updateComment(sessionManager.getAccessToken(), commentIdUpdating, updateCommentParams);
        callbackAddCommentCall.enqueue(new Callback<CallbackAddComment>() {
            @Override
            public void onResponse(Call<CallbackAddComment> call, Response<CallbackAddComment> response) {
                Log.d(TAG, "onResponse: " + response);
                responseAddComment = response.body();
                if (responseAddComment != null) {
                    if (responseAddComment.getSuccess()) {
                        customLoader.hideIndicator();
                        /*commentsAdapter.add(responseAddComment.getComment());
                        et_comment.setText("");
                        tv_no.setVisibility(View.GONE);
                        rv_comments.setVisibility(View.VISIBLE);
                        setResult(RESULT_OK);
                        View view = PostDetailActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }*/
                        et_comment.setText("");
                        img_send.setVisibility(View.VISIBLE);
                        tv_update_comment.setVisibility(View.GONE);
                        recreate();

                    } else {
                        Log.d(TAG, "onResponse: " + responseAddComment.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAddComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    private void addCommentWithMedia() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackAddCommentCall = api.addCommentWithImages(sessionManager.getAccessToken(), addCommentParams, media);
        callbackAddCommentCall.enqueue(new Callback<CallbackAddComment>() {
            @Override
            public void onResponse(Call<CallbackAddComment> call, Response<CallbackAddComment> response) {
                Log.d(TAG, "onResponse: " + response);
                responseAddComment = response.body();
                if (responseAddComment != null) {
                    if (responseAddComment.getSuccess()) {
                        customLoader.hideIndicator();
                        commentsAdapter.add(responseAddComment.getComment());
                        et_comment.setText("");
                        tv_no.setVisibility(View.GONE);
                        rv_comments.setVisibility(View.VISIBLE);
                        setResult(RESULT_OK);
                        View view = SinglePostDetailActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseAddComment.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAddComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Log.d("hhhh", "onActivityResult: " + mediaPaths.size());
            if (mediaPaths.size() > 0) {
                rl_media.setVisibility(View.VISIBLE);
                img_selected.setImageURI(Uri.parse(mediaPaths.get(0)));
            }
        }
    }

    public void showCommentMenu(final Activity activity, View view, final PostComment postComment, final int position, final RecyclerView rv_comments, final List<PostComment> commentsList, final CommentsAdapter commentsAdapter, final TextView tv_no) {
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
                        et_comment.setText(commentsList.get(position).getBody());
                        img_send.setVisibility(View.GONE);
                        tv_update_comment.setVisibility(View.VISIBLE);
                        commentIdUpdating = commentsList.get(position).getId().toString();
                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    private void deleteComment(final Activity activity, Integer id, final int position, final RecyclerView rv_comments, final List<PostComment> commentsList, final CommentsAdapter commentsAdapter, final TextView tv_no) {
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

    private void reportComment(final Activity activity, Integer id) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "reportComment: " + sessionManager.getAccessToken());
        Log.d(TAG, "reportComment: " + id);

        callbackDeleteComment = api.reportComment(String.valueOf(id), sessionManager.getAccessToken());
        callbackDeleteComment.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseDeleteComment = response.body();
                if (responseDeleteComment != null) {
                    if (responseDeleteComment.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseDeleteComment.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (img_send.getVisibility() == View.GONE) {
            img_send.setVisibility(View.VISIBLE);
            tv_update_comment.setVisibility(View.GONE);
            et_comment.setText("");
        } else {
            super.onBackPressed();
        }
    }

}
