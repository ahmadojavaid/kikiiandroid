package com.jobesk.kikiiapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Adapters.CommunityPostsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackSpecificUserPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.Post;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.PaginationScrollListener;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.ShowPopupMenus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecificUserPosts extends Activity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "CommunityFragment";
    public static boolean NEED_TO_LOAD_DATA = true;
    public static final int REQUEST_POST_DETAIL = 245;
    public static final int REQUEST_UPDATE_POST = 24;
    private Context context=SpecificUserPosts.this;
    private Activity activity=SpecificUserPosts.this;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_community_posts;
    private CommunityPostsAdapter communityPostsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Post> communityPostsList = new ArrayList<>();
    private Map<String, String> communityPostsParam = new HashMap<>();
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackSpecificUserPosts> callbackSpecificUserPostsCall;
    private CallbackSpecificUserPosts responseUserPosts;

    private Call<CallbackStatus> callbackLikeCall, callbackDeletePost;
    private CallbackStatus responseLike, responseDeletePost;

    /*****/
    ProgressBar progressBar;

    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 0;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private int TOTAL_PAGES = 3;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;
    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user_posts);

        getIntentData();
        initComponents();
        isLastPage=false;
        isLoading=false;
        currentPage=0;
        loadUserPosts();
        swipeRefreshLayout.setOnRefreshListener(this);

        rv_community_posts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
               /* if (currentPage != -1)
                    loadCommunityPosts();*/
                loadUserPosts();
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

    private void getIntentData() {
        user_id=getIntent().getStringExtra(Constants.ID);
    }

    private void loadUserPosts() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackSpecificUserPostsCall = api.getUserPosts(sessionManager.getAccessToken(), String.valueOf(currentPage),user_id);
        callbackSpecificUserPostsCall.enqueue(new Callback<CallbackSpecificUserPosts>() {
            @Override
            public void onResponse(Call<CallbackSpecificUserPosts> call, Response<CallbackSpecificUserPosts> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUserPosts = response.body();
                if (responseUserPosts != null) {
                    if (responseUserPosts.getSuccess()) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (responseUserPosts.getPosts().size() > 0) {
                            if (currentPage == 0) {
                                tv_no.setVisibility(View.GONE);
                                rv_community_posts.setVisibility(View.VISIBLE);
                                setData();
                            } else {
                                currentPage = responseUserPosts.getNextOffset();
                                if (responseUserPosts.getPosts().size() > 0) {
                                    communityPostsAdapter.addList(responseUserPosts.getPosts());
                                }
                                else {
                                    isLastPage = true;
                                    currentPage = 0;
                                }
                                isLoading = false;
                                customLoader.hideIndicator();
                            }
                        } else {
                            if(currentPage!=0){
                                isLastPage = true;
                                currentPage = 0;
                            }
                            else{
                                tv_no.setVisibility(View.VISIBLE);
                                rv_community_posts.setVisibility(View.GONE);
                            }
                            customLoader.hideIndicator();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseUserPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseUserPosts.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackSpecificUserPosts> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        NEED_TO_LOAD_DATA = false;
        currentPage = responseUserPosts.getNextOffset();
        communityPostsList = responseUserPosts.getPosts();
        communityPostsAdapter.addAll(communityPostsList);
        rv_community_posts.setAdapter(communityPostsAdapter);
        customLoader.hideIndicator();
        communityPostsAdapter.setOnClickListeners(new CommunityPostsAdapter.IClicks() {
            @Override
            public void onLikeDislikeClick(View view, Post post, int position, TextView tv_likes) {
                likeDislikePost(post.getId(), position, tv_likes);
            }

            @Override
            public void onCommentClick(View view, Post post) {
                Intent intent = new Intent(context, PostDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("post", post);
                intent.putExtra("postID", String.valueOf(post.getId()));
//                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_POST_DETAIL);
            }

            @Override
            public void onShareClick(View view, Post post) {

            }
            @Override
            public void onMenuClick(View view, final Post post, final int position) {
                ShowPopupMenus.showPostMenu(activity, view, post, position, rv_community_posts, communityPostsList, communityPostsAdapter);
            }
        });
    }

    private void initComponents() {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        communityPostsAdapter = new CommunityPostsAdapter(context);
        communityPostsAdapter.addAll(communityPostsList);

        rv_community_posts = findViewById(R.id.rv_community_posts);
        layoutManager = new LinearLayoutManager(context);
        rv_community_posts.setLayoutManager(layoutManager);

        tv_no = findViewById(R.id.tv_no);
    }

    private void likeDislikePost(Integer id, final int position, final TextView tv_likes) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackLikeCall = api.likeDislikePost(sessionManager.getAccessToken(), String.valueOf(id));
        callbackLikeCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseLike = response.body();
                if (responseLike != null) {
                    if (responseLike.getSuccess()) {
                        customLoader.hideIndicator();
                        Post post = communityPostsList.get(position);
                        int likeCount = post.getLikesCount();
                        if (post.getIsLiked().toString().equalsIgnoreCase("0")) {
                            likeCount = likeCount + 1;
                            post.setLikesCount(likeCount);
                            post.setIsLiked(1);
                            tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fill_heart, 0, 0, 0);
                        } else {
                            likeCount = likeCount - 1;
                            post.setLikesCount(likeCount);
                            post.setIsLiked(0);
                            tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_empty_heart, 0, 0, 0);
                        }
                        communityPostsList.set(position, post);
                        communityPostsAdapter.notifyItemChanged(position, post);
                    } else {
                        Log.d(TAG, "onResponse: " + responseUserPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseUserPosts.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
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
    public void onRefresh() {
        communityPostsList.clear();
        isLastPage=false;
        isLoading=false;
        currentPage=PAGE_START;
        loadUserPosts();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_POST_DETAIL && resultCode == Activity.RESULT_OK) {
            loadUserPosts();
        }
        if (requestCode == REQUEST_UPDATE_POST && resultCode == Activity.RESULT_OK) {
            loadUserPosts();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }
}
