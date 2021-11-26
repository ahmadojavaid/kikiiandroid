package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jobesk.kikiiapp.Activities.SignUpModule.SignUpActivity;
import com.jobesk.kikiiapp.Activities.SignUpModule.SubscriptionActivityApp;
import com.jobesk.kikiiapp.Netwrok.API;
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

public class SupportActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SupportActivity";
    private Context context = SupportActivity.this;
    private Activity activity = SupportActivity.this;

    private ImageView img_back;
    private TextView tv_logout;
    private SessionManager sessionManager;

    private LinearLayout ll_terms_and_condition, ll_privacy_policy;
    LinearLayout blockedProfilesContainer;
    LinearLayout delete_my_account, unitsContainer;
    private CustomLoader customLoader;
    private LinearLayout languageContainer;
    private LinearLayout upgrade_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initComponents();

        img_back.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        ll_terms_and_condition.setOnClickListener(this);
        ll_privacy_policy.setOnClickListener(this);

        findViewById(R.id.reportContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SupportActivity.this, ReportActivity.class));

            }
        });

    }

    private void initComponents() {

        customLoader = new CustomLoader(SupportActivity.this, false);

        sessionManager = new SessionManager(this);

        img_back = findViewById(R.id.img_back);

        tv_logout = findViewById(R.id.tv_logout);

        ll_privacy_policy = findViewById(R.id.ll_privacy_policy);
        ll_terms_and_condition = findViewById(R.id.ll_terms_and_condition);
        languageContainer = findViewById(R.id.languageContainer);

        blockedProfilesContainer = findViewById(R.id.blockedProfilesContainer);
        delete_my_account = findViewById(R.id.delete_my_account);
        unitsContainer = findViewById(R.id.unitsContainer);

        delete_my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteMyAccount();

            }
        });

        blockedProfilesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SupportActivity.this, BlockedUsersActivity.class));

            }
        });

        unitsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogWithListview();
            }
        });

        languageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLangugaeAlert();
            }
        });

        upgrade_account = findViewById(R.id.upgrade_account);
        upgrade_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SupportActivity.this, SubscriptionActivityApp.class);
                intent.putExtra("from", "support");
                startActivity(intent);

            }
        });

    }

    private void showLangugaeAlert() {

        List<String> mAnimals = new ArrayList<String>();
        mAnimals.add("English");

        //Create sequence of items
        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Select Language");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();  //Selected item in listview


            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }


    private void ShowAlertDialogWithListview() {
        List<String> mAnimals = new ArrayList<String>();
        mAnimals.add("Feet");
        mAnimals.add("cm");

        //Create sequence of items
        final CharSequence[] Animals = mAnimals.toArray(new String[mAnimals.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Height Units");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();  //Selected item in listview

                if (selectedText.equalsIgnoreCase("Feet")) {
                    sessionManager.saveHeightUnit("Feet");
                } else {
                    sessionManager.saveHeightUnit("cm");
                }


            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public void DeleteMyAccount() {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getApplicationContext());

        String id = sessionManager.getUserID();

        Call<JsonObject> call = api.deleteMyAccount(sessionManager.getAccessToken(), String.valueOf((id)));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("responseBlockedUsers", "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();

                if (response.isSuccessful()) {
                    sessionManager.logoutUser();
                    Intent intent = new Intent(SupportActivity.this, SignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SupportActivity.this, "Request Submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(SupportActivity.this);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
            case R.id.tv_logout:
                sessionManager.logoutUser();
                finish();
                break;
            case R.id.ll_terms_and_condition:
                startActivity(new Intent(context, TermsAndConditionsActivity.class));
                break;
            case R.id.ll_privacy_policy:
                startActivity(new Intent(context, PrivacyPolicyActivity.class));
                break;

        }
    }
}
