package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jobesk.kikiiapp.Adapters.BlockedUserAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.BlockedUserModel.BlockedUser;
import com.jobesk.kikiiapp.Model.BlockedUserModel.BlockedUserModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.recyclerviewCallbacks.BlocledUserInterfaceCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockedUsersActivity extends AppCompatActivity implements BlocledUserInterfaceCallback {

    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private RecyclerView recycler_view;
    private BlockedUserAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);


        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customLoader = new CustomLoader(BlockedUsersActivity.this, false);
        sessionManager = new SessionManager(getApplicationContext());
        recycler_view = findViewById(R.id.recycler_view);

        getBlockedUsers();

    }

    private void populateRecyclerView(List<BlockedUser> listBlockedUser) {

        mAdapter = new BlockedUserAdapter(BlockedUsersActivity.this, listBlockedUser);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);

    }

    public void getBlockedUsers() {


        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getApplicationContext());

        Call call = api.getBlockedUsers(sessionManager.getAccessToken());
        call.enqueue(new Callback<BlockedUserModel>() {
            @Override
            public void onResponse(Call<BlockedUserModel> call, Response<BlockedUserModel> response) {
                Log.d("responseBlockedUsers", "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();

                if (response.isSuccessful()) {


                    BlockedUserModel model = response.body();
                    List<BlockedUser> listBlockedUser = model.getBlockedUsers();
                    populateRecyclerView(listBlockedUser);

                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(BlockedUsersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<BlockedUserModel> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d("asd", "onResponse: " + t.getMessage());

                }
            }
        });
    }


    @Override
    public void onClick(int position, int id) {
        Log.d("TAG", "onClick: " + id);
        unBlockUser(id);

        Intent intent = new Intent("refresh_for_block");
        LocalBroadcastManager.getInstance(BlockedUsersActivity.this).sendBroadcast(intent);
    }


    public void unBlockUser(int id) {


        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getApplicationContext());

        Call call = api.unBlockUser(sessionManager.getAccessToken(), String.valueOf((id)));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("responseBlockedUsers", "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();

                if (response.isSuccessful()) {





                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(BlockedUsersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d("asd", "onResponse: " + t.getMessage());

                }
            }
        });
    }
}