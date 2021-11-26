package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {
    private Button btn_save;
    private EditText messageEt, titleEt;
    private CustomLoader customLoader;
    private String titleValue, messageValue;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        customLoader = new CustomLoader(ReportActivity.this, false);
        sessionManager = new SessionManager(getApplicationContext());
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        btn_save = findViewById(R.id.btn_save);
        messageEt = findViewById(R.id.messageEt);
        titleEt = findViewById(R.id.titleEt);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                titleValue = titleEt.getText().toString().trim();
                messageValue = messageEt.getText().toString().trim();

                if (titleValue.equalsIgnoreCase("")) {
                    Toast.makeText(ReportActivity.this, "Please Enter Title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (messageValue.equalsIgnoreCase("")) {
                    Toast.makeText(ReportActivity.this, "Please Enter Your Message", Toast.LENGTH_SHORT).show();
                    return;
                }


                reportAProblem();

            }
        });


    }


    private void reportAProblem() {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getApplicationContext());
        Log.d("TAG", "loadCommunityPosts: " + sessionManager.getAccessToken());
        Call callback = api.reportProblem(sessionManager.getAccessToken(), titleValue, messageValue);
        callback.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                Log.d("reportsubmit", "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();
                if (response.isSuccessful()) {

                    Toast.makeText(ReportActivity.this, "Submitted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                customLoader.hideIndicator();
                Log.d("TAG", "onResponse: " + t.getMessage());
            }
        });
    }
}