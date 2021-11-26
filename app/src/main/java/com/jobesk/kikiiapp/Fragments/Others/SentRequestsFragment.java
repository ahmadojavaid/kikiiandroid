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

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Activities.BlockedUsersActivity;
import com.jobesk.kikiiapp.Adapters.FriendsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetSentRequests;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.FellowUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SentRequestsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "CommunityFragment";
    private Context context;
    private Activity activity;


    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_sent_requests;
    private FriendsAdapter friendsAdapter;
    private List<FellowUser> sentRequestsList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetSentRequests> callbackGetSentRequests;
    private CallbackGetSentRequests responseSentRequests;

    private Call<CallbackStatus> callbackStatusCall;
    private CallbackStatus responseStatus;

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

    private Map<String, String> paramsList = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sent_requests, container, false);

        initComponents(view);
        loadSentRequests();

        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    private void loadSentRequests() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadSentRequests: " + sessionManager.getAccessToken());
        callbackGetSentRequests = api.getSentRequests(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetSentRequests.enqueue(new Callback<CallbackGetSentRequests>() {
            @Override
            public void onResponse(Call<CallbackGetSentRequests> call, Response<CallbackGetSentRequests> response) {
                Log.d(TAG, "onResponse: " + response);
                responseSentRequests = response.body();
                if (responseSentRequests != null) {
                    customLoader.hideIndicator();
                    swipeRefreshLayout.setRefreshing(false);
                    if (responseSentRequests.getFellowUsers() != null) {
                        if (responseSentRequests.getFellowUsers().size() > 0) {
                            tv_no.setVisibility(View.GONE);
                            rv_sent_requests.setVisibility(View.VISIBLE);
                            setData();
                        } else {
                            tv_no.setVisibility(View.VISIBLE);
                            rv_sent_requests.setVisibility(View.GONE);
                        }
                    } else {
                        tv_no.setVisibility(View.VISIBLE);
                        rv_sent_requests.setVisibility(View.GONE);
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetSentRequests> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        currentPage = responseSentRequests.getNextOffset();
        sentRequestsList = responseSentRequests.getFellowUsers();
        friendsAdapter.addAll(sentRequestsList);
        rv_sent_requests.setAdapter(friendsAdapter);

    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        rv_sent_requests = view.findViewById(R.id.rv_sent_requests);
        layoutManager = new LinearLayoutManager(context);
        rv_sent_requests.setLayoutManager(layoutManager);
        friendsAdapter = new FriendsAdapter("requests", sentRequestsList, getContext());
        rv_sent_requests.setAdapter(friendsAdapter);

        tv_no = view.findViewById(R.id.tv_no);

        friendsAdapter.setOnClickListener(new FriendsAdapter.IClicks() {
            @Override
            public void onFollowUser(View view, FellowUser user) {

            }
            @Override
            public void onUnFollowUser(View view, FellowUser user) {

                Intent intent = new Intent("refresh_for_block");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                unFollowUser(user);
            }
        });
    }

    private void unFollowUser(FellowUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.unFollowUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (!responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());


                        loadSentRequests();
                    }
                    customLoader.hideIndicator();
                    Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
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
        loadSentRequests();
    }
}
