package com.jobesk.kikiiapp.Fragments.Others;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.FriendsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetPendingRequests;
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

public class PendingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "PendingFragment";
    private Context context;
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_pending;
    private FriendsAdapter friendsAdapter;
    private List<FellowUser> pendingList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetPendingRequests> callbackGetPendingRequestsCall;
    private CallbackGetPendingRequests responsePendingRequests;

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

        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        initComponents(view);
        loadPendingRequests();


        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    private void loadPendingRequests() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackGetPendingRequestsCall = api.getPendingRequests(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetPendingRequestsCall.enqueue(new Callback<CallbackGetPendingRequests>() {
            @Override
            public void onResponse(Call<CallbackGetPendingRequests> call, Response<CallbackGetPendingRequests> response) {
                Log.d("Pending_response", "onResponse: " + new Gson().toJson(response.body()));
                responsePendingRequests = response.body();
                if (responsePendingRequests != null) {
                    customLoader.hideIndicator();
                    swipeRefreshLayout.setRefreshing(false);
                    if (responsePendingRequests.getFellowUsers() != null) {
                        if (responsePendingRequests.getFellowUsers().size() > 0) {
                            tv_no.setVisibility(View.GONE);
                            rv_pending.setVisibility(View.VISIBLE);
                            setData();
                        } else {
                            tv_no.setVisibility(View.VISIBLE);
                            rv_pending.setVisibility(View.GONE);
                        }
                    } else {
                        tv_no.setVisibility(View.VISIBLE);
                        rv_pending.setVisibility(View.GONE);
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetPendingRequests> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        currentPage = responsePendingRequests.getNextOffset();
        pendingList = responsePendingRequests.getFellowUsers();
        friendsAdapter.addAll(pendingList);
        rv_pending.setAdapter(friendsAdapter);

    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        rv_pending = view.findViewById(R.id.rv_pending);
        layoutManager = new LinearLayoutManager(context);
        rv_pending.setLayoutManager(layoutManager);
        friendsAdapter = new FriendsAdapter("pendings", pendingList, getContext());
        rv_pending.setAdapter(friendsAdapter);

        tv_no = view.findViewById(R.id.tv_no);

        friendsAdapter.setOnClickListener(new FriendsAdapter.IClicks() {
            @Override
            public void onFollowUser(View view, FellowUser user) {
                followUser(user);
            }

            @Override
            public void onUnFollowUser(View view, FellowUser user) {

            }
        });
    }

    private void followUser(FellowUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.followUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (!responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        loadPendingRequests();
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
        loadPendingRequests();
    }
}
