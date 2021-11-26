package com.jobesk.kikiiapp.Fragments.Others;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Activities.PostDetailActivity;
import com.jobesk.kikiiapp.Adapters.CommunityPostsAdapter;
import com.jobesk.kikiiapp.Callbacks.Ad;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCommunityPosts;
import com.jobesk.kikiiapp.Callbacks.CallbackSinglePost;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.Post;
import com.jobesk.kikiiapp.Model.PostMedia;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.PaginationScrollListener;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.ShowPopupMenus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "CommunityFragment";
    public static boolean NEED_TO_LOAD_DATA = true;
    public static final int REQUEST_POST_DETAIL = 245;
    public static final int REQUEST_UPDATE_POST = 24;
    private Context context;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;
    private RecyclerView rv_community_posts;
    private CommunityPostsAdapter communityPostsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Post> communityPostsList = new ArrayList<>();
    private Map<String, String> communityPostsParam = new HashMap<>();
    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private Call<CallbackGetCommunityPosts> callbackGetCommunityPostsCall;
    private CallbackGetCommunityPosts responseAllPosts;
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
    private Uri postLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        initComponents(view);
        //if (NEED_TO_LOAD_DATA)

        isLastPage = false;
        isLoading = false;
        currentPage = 0;


        loadCommunityPosts();
        swipeRefreshLayout.setOnRefreshListener(this);


        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiverRefresh,
                new IntentFilter("refresh_list"));
        return view;
    }


    private BroadcastReceiver mMessageReceiverRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            String message = intent.getStringExtra("message");
//            Log.d("receiver", "Got message: " + message);
            isLastPage = false;
            isLoading = false;
            currentPage = 0;

            if (communityPostsList.size() > 0) {
                communityPostsList.clear();
            }

            loadCommunityPosts();
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rv_community_posts.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
               /* if (currentPage != -1)
                    loadCommunityPosts();*/
                loadCommunityPosts();
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


    private void loadCommunityPosts() {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetCommunityPostsCall = api.getAllPosts(sessionManager.getAccessToken(), String.valueOf(currentPage));
        callbackGetCommunityPostsCall.enqueue(new Callback<CallbackGetCommunityPosts>() {
            @Override
            public void onResponse(Call<CallbackGetCommunityPosts> call, Response<CallbackGetCommunityPosts> response) {
                Log.d("communityResposeHere", "onResponse: " + new Gson().toJson(response.body()));
                responseAllPosts = response.body();
                if (responseAllPosts != null) {
                    if (responseAllPosts.getSuccess()) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (responseAllPosts.getPosts().size() > 0) {
                            if (currentPage == 0) {
                                tv_no.setVisibility(View.GONE);
                                rv_community_posts.setVisibility(View.VISIBLE);
                                setData();
                            } else {
                                currentPage = responseAllPosts.getNextOffset();
                                if (responseAllPosts.getPosts().size() > 0) {
                                    communityPostsAdapter.addList(responseAllPosts.getPosts());

//                                    List<Ad> adsList = responseAllPosts.getAds();
//                                    Ad ad = adsList.get(0);
//                                    String link = ad.getLink();
//                                    String path = ad.getPath();
//
//                                    PostMedia media = new PostMedia();
//
//                                    List<PostMedia> list = new ArrayList<>();
//                                    for (int i = 1; i <= 100; i++) {
//
//                                        media.setId(1);
//                                        media.setPath("a");
//                                        media.setPostId(1);
//                                        media.setUserId(1);
//
//                                    }
//
//                                    Post post = new Post();
//                                    post.setBody(link);
//                                    post.setUpdatedAt(path);
//                                    post.setMedia(list);

//                                    communityPostsAdapter.add(post);


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
                                rv_community_posts.setVisibility(View.GONE);
                            }
                            customLoader.hideIndicator();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseAllPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAllPosts.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetCommunityPosts> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });

    }

    private void setData() {

        NEED_TO_LOAD_DATA = false;
        currentPage = responseAllPosts.getNextOffset();
        communityPostsList = responseAllPosts.getPosts();
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
                intent.putExtra("postID", String.valueOf(post.getId()));
                //    bundle.putSerializable("post", post);
//                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_POST_DETAIL);
            }

            @Override
            public void onShareClick(View view, Post post) {


                customLoader.showIndicator();
                createShareLink(post);
            }

            @Override
            public void onMenuClick(View view, final Post post, final int position) {
                ShowPopupMenus.showPostMenu(activity, view, post, position, rv_community_posts, communityPostsList, communityPostsAdapter);
            }
        });
    }

    private void createShareLink(Post post) {

        String packageName = activity.getPackageName();
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/?post_id=" + post.getId()))
                .setDynamicLinkDomain("kikiiapp1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
//                                .setMinimumVersion(125)
                                .build())
                // Open links with com.example.ios on iOS
                .buildDynamicLink();
        postLink = dynamicLink.getUri();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(postLink)
                .buildShortDynamicLink()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            customLoader.hideIndicator();
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.d(TAG, "onComplete: " + shortLink);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            startActivity(Intent.createChooser(intent, "Share"));
                        } else {
                            customLoader.hideIndicator();
                            Log.d(TAG, "ERROR: " + task.getException());
                        }
                    }
                });
    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        communityPostsAdapter = new CommunityPostsAdapter(context);
        communityPostsAdapter.addAll(communityPostsList);

        rv_community_posts = view.findViewById(R.id.rv_community_posts);
        layoutManager = new LinearLayoutManager(context);
        rv_community_posts.setLayoutManager(layoutManager);

        tv_no = view.findViewById(R.id.tv_no);


        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("update_comment"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String postID = intent.getStringExtra("postID");
            String type = intent.getStringExtra("type");
            Log.d("receiver", "Got message: " + postID);

            for (int i = 0; i < communityPostsList.size(); i++) {
                if (postID.equalsIgnoreCase(communityPostsList.get(i).getId().toString())) {
                    int commentCount = communityPostsList.get(i).getCommentsCount();
                    if (type.equalsIgnoreCase("increment")) {
                        commentCount = commentCount + 1;
                        communityPostsList.get(i).setCommentsCount(commentCount);
                        communityPostsAdapter.notifyItemChanged(i);
                    } else {
                        commentCount = commentCount - 1;
                        communityPostsList.get(i).setCommentsCount(commentCount);
                        communityPostsAdapter.notifyItemChanged(i);
                    }
                    break;
                }

            }
        }
    };


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
                        Log.d(TAG, "onResponse: " + responseAllPosts.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAllPosts.getMessage(), Toast.LENGTH_SHORT).show();
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
        isLastPage = false;
        isLoading = false;
        currentPage = PAGE_START;
        loadCommunityPosts();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_POST_DETAIL && resultCode == Activity.RESULT_OK) {
            loadCommunityPosts();
        }
        if (requestCode == REQUEST_UPDATE_POST && resultCode == Activity.RESULT_OK) {
            loadCommunityPosts();
        }
    }

}
