package com.jobesk.kikiiapp.Fragments.Others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Activities.KikiiPostDetailActivity;
import com.jobesk.kikiiapp.Adapters.KikiiPostsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetKikiiPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.KikiiPost;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.PaginationScrollListener;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KikiiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "KikiiFragment";
    private Context context;
    private Activity activity;
    public static final int REQUEST_POST_DETAIL = 244;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_kikii_posts;
    private KikiiPostsAdapter kikiiPostsAdapter;
    private List<KikiiPost> kikiiPostsList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetKikiiPosts> callbackGetKikiiPostsCall;
    private CallbackGetKikiiPosts responseGetKikiiPosts;

    private Call<CallbackStatus> callbackLikeCall, callbackDeletePost;
    private CallbackStatus responseLike, responseDeletePost;
    private boolean NEED_TO_LOAD_DATA = false;

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

    /*****/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kikii, container, false);

        isLastPage = false;
        isLoading = false;
        currentPage = 0;

        initComponents(view);
        getKikiiPosts();

        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rv_kikii_posts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
               /* if (currentPage != -1)
                    loadCommunityPosts();*/
                getKikiiPosts();
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


    private void getKikiiPosts() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackGetKikiiPostsCall = api.getKikiiPosts(sessionManager.getAccessToken(), String.valueOf(currentPage));
        callbackGetKikiiPostsCall.enqueue(new Callback<CallbackGetKikiiPosts>() {
            @Override
            public void onResponse(Call<CallbackGetKikiiPosts> call, Response<CallbackGetKikiiPosts> response) {
                Log.d(TAG, "onResponse: " + response);
                responseGetKikiiPosts = response.body();
                if (responseGetKikiiPosts != null) {
                    if (responseGetKikiiPosts.getSuccess()) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (responseGetKikiiPosts.getPosts().size() > 0) {
                            if (currentPage == 0) {
                                tv_no.setVisibility(View.GONE);
                                rv_kikii_posts.setVisibility(View.VISIBLE);
                                setData();
                            } else {
                                currentPage = responseGetKikiiPosts.getNextOffset();
                                if (responseGetKikiiPosts.getPosts().size() > 0) {
                                    kikiiPostsAdapter.addList(responseGetKikiiPosts.getPosts());
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
                                rv_kikii_posts.setVisibility(View.GONE);
                            }
                            customLoader.hideIndicator();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetKikiiPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetKikiiPosts.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetKikiiPosts> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        NEED_TO_LOAD_DATA = false;
        currentPage = responseGetKikiiPosts.getNextOffset();
        kikiiPostsList = responseGetKikiiPosts.getPosts();
        kikiiPostsAdapter.addAll(kikiiPostsList);
        rv_kikii_posts.setAdapter(kikiiPostsAdapter);
        customLoader.hideIndicator();
        kikiiPostsAdapter.setOnClickListeners(new KikiiPostsAdapter.IClicks() {
            @Override
            public void onLikeDislikeClick(View view, KikiiPost post, int position, TextView tv_likes) {
                likeDislikePost(post.getId(), position, tv_likes);

            }

            @Override
            public void onCommentClick(View view, KikiiPost post) {
                Intent intent = new Intent(context, KikiiPostDetailActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putSerializable("post", post);
                bundle.putString("postID", String.valueOf(post.getId()));
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_POST_DETAIL);
            }
        });
    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        kikiiPostsAdapter = new KikiiPostsAdapter(context);
        kikiiPostsAdapter.addAll(kikiiPostsList);

        rv_kikii_posts = view.findViewById(R.id.rv_kikii_posts);
        layoutManager = new LinearLayoutManager(context);
        rv_kikii_posts.setLayoutManager(layoutManager);

        tv_no = view.findViewById(R.id.tv_no);

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
                        KikiiPost post = kikiiPostsList.get(position);
                        int likeCount = post.getLikes_count();
                        if (post.getIsLiked().toString().equalsIgnoreCase("0")) {
                            likeCount = likeCount + 1;
                            post.setLikes_count(likeCount);
                            post.setIsLiked(1);
                            tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fill_heart, 0, 0, 0);
                        } else {
                            likeCount = likeCount - 1;
                            post.setLikes_count(likeCount);
                            post.setIsLiked(0);
                            tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_empty_heart, 0, 0, 0);
                        }
                        kikiiPostsList.set(position, post);
                        kikiiPostsAdapter.notifyItemChanged(position, post);
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetKikiiPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetKikiiPosts.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_POST_DETAIL && resultCode == Activity.RESULT_OK) {
            getKikiiPosts();
        }
    }

    @Override
    public void onRefresh() {
        kikiiPostsList.clear();
        isLastPage = false;
        isLoading = false;
        currentPage = PAGE_START;
        getKikiiPosts();
    }
}
