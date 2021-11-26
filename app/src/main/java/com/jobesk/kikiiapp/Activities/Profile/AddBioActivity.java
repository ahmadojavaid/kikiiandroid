package com.jobesk.kikiiapp.Activities.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddBioActivity";
    private Context context=AddBioActivity.this;
    private Activity activity=AddBioActivity.this;

    private Call<CallbackUpdateProfile> callbackStatusCall;
    private CallbackUpdateProfile responseLatLongUpdate;
    private Map<String,String> updateParam=new HashMap();
    private EditText et_bio;
    private Button btn_save;
    private SessionManager sessionManager;
    private CustomLoader customLoader;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bio);

        initComponents();

        img_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    private void initComponents() {
        sessionManager=new SessionManager(this);
        customLoader=new CustomLoader(this,false);

        et_bio=findViewById(R.id.et_bio);

        btn_save=findViewById(R.id.btn_save);

        img_back=findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                if(et_bio.getText().toString().trim().isEmpty()){
                    et_bio.setError(getResources().getString(R.string.et_error));
                }
                else{
                    Intent intent=new Intent();
                    intent.putExtra(Constants.BIO,et_bio.getText().toString());
                    setResult(RESULT_OK,intent);
                    onBackPressed();
                    /*updateParam.put(Constant.BIO,et_bio.getText().toString());
                    updateBio();*/
                }
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }

    private void updateBio() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "sendOTP: "+ updateParam.size());
        Log.d(TAG, "token: "+sessionManager.getAccessToken());
        callbackStatusCall = api.updateProfile(sessionManager.getAccessToken(), updateParam);
        callbackStatusCall.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseLatLongUpdate = response.body();
                if(responseLatLongUpdate != null){
                    if (responseLatLongUpdate.getSuccess()) {
                        Log.d(TAG, "onResponse: "+ responseLatLongUpdate.getMessage());
                        customLoader.hideIndicator();
                        onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: "+ responseLatLongUpdate.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseLatLongUpdate.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }
            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }
}
