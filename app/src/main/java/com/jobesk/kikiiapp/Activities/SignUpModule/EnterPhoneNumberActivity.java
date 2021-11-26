package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.jobesk.kikiiapp.Callbacks.CallbackSentOTP;

import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

@SuppressLint("LongLogTag")
public class EnterPhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EnterPhoneNumberActivity";
    private Context mContext = EnterPhoneNumberActivity.this;
    private Button btn_next;
    private CountryCodePicker cpp_countries;
    private TextView tv_country_code;
    private CustomLoader customLoader;

    private Call<CallbackSentOTP> callbackSentOTPCall;
    private CallbackSentOTP responseSentOTP;
    private String countryCode, phone;
    private EditText et_phone_number;
    private SessionManager sessionManager;
    private Map<String, String> sendOTPParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);

        initComponents();

        btn_next.setOnClickListener(this);
        cpp_countries.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {

            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
                tv_country_code.setText(cpp_countries.getSelectedCountryCodeWithPlus());
                countryCode = cpp_countries.getSelectedCountryCodeWithPlus();
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {

            }
        });
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        btn_next = findViewById(R.id.btn_next);

        cpp_countries = findViewById(R.id.cpp_countries);

        tv_country_code = findViewById(R.id.tv_country_code);
        countryCode = cpp_countries.getSelectedCountryCodeWithPlus();
        tv_country_code.setText(countryCode);

        et_phone_number = findViewById(R.id.et_phone_number);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (et_phone_number.getText().toString().isEmpty()) {
                    et_phone_number.setError(getString(R.string.et_error));
                } else {
                    phone = countryCode + et_phone_number.getText().toString();
                    sendOTPParams.put(Constants.PHONE, phone);
                    sendForPhoneVerification();
                }
                break;
        }
    }

    private void sendForPhoneVerification() {
        Intent intent = new Intent(mContext, VerifyOTPActivity.class);
        intent.putExtra("phoneNumber", phone);
        startActivity(intent);
    }
   /* public void sendOTP() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: "+phone);
        callbackSentOTPCall = api.sendOTP(sendOTPParams);
        callbackSentOTPCall.enqueue(new Callback<CallbackSentOTP>() {
            @Override
            public void onResponse(Call<CallbackSentOTP> call, Response<CallbackSentOTP> response) {
                Log.d(TAG, "onResponse: " + response);
                responseSentOTP = response.body();
                if(responseSentOTP != null){
                    if (responseSentOTP.getSuccess()) {
                        Log.d(TAG, "onResponse: "+responseSentOTP.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseSentOTP.getMessage(), Toast.LENGTH_SHORT).show();
                        sessionManager.saveAccessToken("Bearer "+responseSentOTP.getUser().getAuthToken());
                        startActivity(new Intent(mContext, VerifyOTPActivity.class));
                        sendOTPParams.clear();
                    } else {
                        Log.d(TAG, "onResponse: "+responseSentOTP.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseSentOTP.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }
            @Override
            public void onFailure(Call<CallbackSentOTP> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }*/
}
