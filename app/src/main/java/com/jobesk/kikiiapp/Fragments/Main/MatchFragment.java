package com.jobesk.kikiiapp.Fragments.Main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.MatchLikesAdapter;
import com.jobesk.kikiiapp.Adapters.YourMatchAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMatch;
import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.jobesk.kikiiapp.Model.Match;
import com.jobesk.kikiiapp.Model.MatchLike;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.ItemDecorator;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MatchFragment";

    private Context context;
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;
    private RecyclerView rv_your_matches, rv_likes;
    private LinearLayoutManager layoutManager;
    private YourMatchAdapter yourMatchAdapter;
    private List<Match> yourMatchesList = new ArrayList<>();
    private List<MatchLike> yourLikesList = new ArrayList<>();

    public static boolean NEED_TO_LOAD_DATA = true;


    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetMatch> callbackGetMatchCall;
    private CallbackGetMatch responseGetMatch;
    private TextView tv_total_likes, tv_upgrade_text;
    private ImageView img_blur_users, img_add;
    private FirebaseUserModel firebaseUserModel;

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

        View view = inflater.inflate(R.layout.fragment_match, container, false);

        initComponents(view);

        getMatchData();

        return view;
    }

    private void getMatchData() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetMatchCall = api.getMatch(sessionManager.getAccessToken());
        callbackGetMatchCall.enqueue(new Callback<CallbackGetMatch>() {
            @Override
            public void onResponse(Call<CallbackGetMatch> call, Response<CallbackGetMatch> response) {
                Log.d("getMatchesHere", "onResponse: " + new Gson().toJson(response.body()));
                responseGetMatch = response.body();
                if (responseGetMatch != null) {
                    if (responseGetMatch.getSuccess()) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (responseGetMatch.getLikes().size() > 0) {
                            rv_likes.setVisibility(View.VISIBLE);
                            setLikesData();
                        } else {
                            rv_likes.setVisibility(View.GONE);
                        }
                        if (responseGetMatch.getMatches().size() > 0) {
                            tv_no.setVisibility(View.GONE);
                            rv_your_matches.setVisibility(View.VISIBLE);
                            setMatchData();
                        } else {
                            customLoader.hideIndicator();
                            tv_no.setVisibility(View.VISIBLE);
                            rv_your_matches.setVisibility(View.GONE);
                        }

                        /*else
                            currentPage = -1;*/
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetMatch.getMessage());
                        Toast.makeText(context, responseGetMatch.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetMatch> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setMatchData() {

        yourMatchesList = responseGetMatch.getMatches();
        rv_your_matches.setLayoutManager(new GridLayoutManager(context, 2));
        rv_your_matches.setAdapter(new YourMatchAdapter(yourMatchesList, context, activity));
        customLoader.hideIndicator();

    }

    private void setLikesData() {
        if (sessionManager.getProfileUser().getUpgraded() == 1) {
            yourLikesList = responseGetMatch.getLikes();
            tv_total_likes.setText(yourLikesList.size() + " Likes");
            if (yourLikesList.size() > 5) {
                yourLikesList.add(4, new MatchLike(-1));
            }
            img_blur_users.setVisibility(View.GONE);
            tv_upgrade_text.setVisibility(View.GONE);
            rv_likes.setVisibility(View.VISIBLE);
            tv_total_likes.setVisibility(View.VISIBLE);

            rv_likes.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            rv_likes.setAdapter(new MatchLikesAdapter(yourLikesList, context));
            rv_likes.addItemDecoration(new ItemDecorator(-20));
        } else {
            img_blur_users.setVisibility(View.VISIBLE);
            tv_upgrade_text.setVisibility(View.VISIBLE);
            rv_likes.setVisibility(View.GONE);
            tv_total_likes.setVisibility(View.GONE);
        }
    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        rv_your_matches = view.findViewById(R.id.rv_your_matches);
        rv_likes = view.findViewById(R.id.rv_likes);

        tv_no = view.findViewById(R.id.tv_no);
        tv_total_likes = view.findViewById(R.id.tv_total_likes);
        tv_upgrade_text = view.findViewById(R.id.tv_upgrade_text);

        img_blur_users = view.findViewById(R.id.img_blur_users);
        img_add = view.findViewById(R.id.img_add);
        img_add.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        yourMatchesList.clear();
        yourLikesList.clear();
        getMatchData();
    }
}
