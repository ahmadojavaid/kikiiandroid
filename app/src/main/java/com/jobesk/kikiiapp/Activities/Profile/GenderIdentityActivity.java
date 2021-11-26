package com.jobesk.kikiiapp.Activities.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.IdentityAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCategory;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenderIdentityActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GenderIdentity";
    private Context context = GenderIdentityActivity.this;

    private RecyclerView rv_gender_identities;
    private IdentityAdapter identityAdapter;
    private List<String> genderIdentitiesList = new ArrayList<>();
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetCategory> callbackGetCategoryCall;
    private CallbackGetCategory responseGetCategory;
    private String isChecked;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_identity);

        initComponents();
        getIdentity();

        img_back.setOnClickListener(this);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.GENDER_IDENTITY, isChecked);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }

    private void getIdentity() {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetCategoryCall = api.getCategory(sessionManager.getAccessToken(), Constants.GENDER_IDENTITY);
        callbackGetCategoryCall.enqueue(new Callback<CallbackGetCategory>() {
            @Override
            public void onResponse(Call<CallbackGetCategory> call, Response<CallbackGetCategory> response) {
                    customLoader.hideIndicator();
                Log.d("getIdentity", "onResponse: " + new Gson().toJson(response.body()));
                responseGetCategory = response.body();
                if (responseGetCategory != null) {
                    if (responseGetCategory.getSuccess()) {

                        if (responseGetCategory.getValue() != null) {
                            if (responseGetCategory.getValue().getValueAttr().size() > 0)
                                setData();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetCategory.getMessage());

                        Toast.makeText(context, responseGetCategory.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {

                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetCategory> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());

                }
            }
        });
    }

    private void setData() {
        genderIdentitiesList = responseGetCategory.getValue().getValueAttr();
        identityAdapter = new IdentityAdapter(genderIdentitiesList, context, responseGetCategory.getIsChecked());
        rv_gender_identities.setAdapter(identityAdapter);
        identityAdapter.setOnClickListener(new IdentityAdapter.IClicks() {
            @Override
            public void onClickListener(View view, String s) {
                isChecked = s;
            }
        });
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        rv_gender_identities = findViewById(R.id.rv_gender_identities);
        rv_gender_identities.setLayoutManager(new LinearLayoutManager(context));

        img_back = findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }
}
