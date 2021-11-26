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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.FriendsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMyFriends;
import com.jobesk.kikiiapp.Model.FellowUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyFriendsFragment";
    private Context context;
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_my_friends;
    private FriendsAdapter friendsAdapter;
    private List<FellowUser> myFriendsList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetMyFriends> callbackGetMyFriends;
    private CallbackGetMyFriends responseMyFriends;
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

        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);

        initComponents(view);
        loadMyFriends();

        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    private void loadMyFriends() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadMyFriends: " + sessionManager.getAccessToken());
        callbackGetMyFriends = api.getMyFriends(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetMyFriends.enqueue(new Callback<CallbackGetMyFriends>() {
            @Override
            public void onResponse(Call<CallbackGetMyFriends> call, Response<CallbackGetMyFriends> response) {
                Log.d("myfriendsHeree", "onResponse: " + new Gson().toJson(response.body()));
                responseMyFriends = response.body();
                if (responseMyFriends != null) {
                    customLoader.hideIndicator();
                    swipeRefreshLayout.setRefreshing(false);
                    if (responseMyFriends.getFellowUsers() != null) {
                        if (responseMyFriends.getFellowUsers().size() > 0) {
                            tv_no.setVisibility(View.GONE);
                            rv_my_friends.setVisibility(View.VISIBLE);
                            setData();
                        }
                    /*else
                    currentPage = -1;*/
                        else {
                            tv_no.setVisibility(View.VISIBLE);
                            rv_my_friends.setVisibility(View.GONE);
                        }
                    } else {
                        tv_no.setVisibility(View.VISIBLE);
                        rv_my_friends.setVisibility(View.GONE);
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetMyFriends> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        currentPage = responseMyFriends.getNextOffset();
        myFriendsList = responseMyFriends.getFellowUsers();
        friendsAdapter.addAll(myFriendsList);
        rv_my_friends.setAdapter(friendsAdapter);

    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        rv_my_friends = view.findViewById(R.id.rv_my_friends);
        layoutManager = new LinearLayoutManager(context);
        rv_my_friends.setLayoutManager(layoutManager);
        friendsAdapter = new FriendsAdapter("myFriends", myFriendsList, getContext());
        rv_my_friends.setAdapter(friendsAdapter);

        tv_no = view.findViewById(R.id.tv_no);

    }

    @Override
    public void onRefresh() {
        loadMyFriends();
    }
}
