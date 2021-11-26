package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Activities.MainActivity;
import com.jobesk.kikiiapp.Callbacks.CallbackContinueWithPhone;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Callbacks.CallbackVerifyOTP;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Firebase.ChangeEventListener;
import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.jobesk.kikiiapp.Firebase.Services.UserService;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OTPActivity";
    private Context mContext = VerifyOTPActivity.this;
    private EditText et_otp_1, et_otp_2, et_otp_3, et_otp_4, et_otp_5, et_otp_6;
    private Button btn_next;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackVerifyOTP> callbackVerifyOTPCall;
    private CallbackVerifyOTP responseVerifyOTP;

    private Call<CallbackContinueWithPhone> callbackContinueWithPhoneCall;
    private CallbackContinueWithPhone responseContinueWithPhone;

    private Call<CallbackStatus> callbackStatusCall;
    private CallbackStatus responseResendCode;

    private String code;
    private Map<String, String> verifyOTPParams = new HashMap<>();
    private LinearLayout ll_timer, ll_resend;
    private TextView tv_time, tv_resend_code;
    private String phoneNumber;
    private boolean connected;
    private FirebaseAuth mAuth;
    private UserService userService;
    private String mVerificationId;
    private Map<String, String> sendOTPParams = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        getIntentData();
        initComponents();
        startCountDown();

        et_otp_1.addTextChangedListener(new GenericTextWatcher(et_otp_1));
        et_otp_2.addTextChangedListener(new GenericTextWatcher(et_otp_2));
        et_otp_3.addTextChangedListener(new GenericTextWatcher(et_otp_3));
        et_otp_4.addTextChangedListener(new GenericTextWatcher(et_otp_4));
        et_otp_5.addTextChangedListener(new GenericTextWatcher(et_otp_5));
        et_otp_6.addTextChangedListener(new GenericTextWatcher(et_otp_6));

        btn_next.setOnClickListener(this);
        tv_resend_code.setOnClickListener(this);
    }

    private void getIntentData() {
        phoneNumber = getIntent().getStringExtra("phoneNumber");
    }

    private void startCountDown() {
        ll_resend.setVisibility(View.GONE);
        ll_timer.setVisibility(View.VISIBLE);
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                tv_time.setText(text);
            }

            @Override
            public void onFinish() {
                ll_resend.setVisibility(View.VISIBLE);
                ll_timer.setVisibility(View.GONE);
            }
        }.start();
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);

        sessionManager = new SessionManager(this);

        et_otp_1 = findViewById(R.id.et_otp_1);
        et_otp_2 = findViewById(R.id.et_otp_2);
        et_otp_3 = findViewById(R.id.et_otp_3);
        et_otp_4 = findViewById(R.id.et_otp_4);
        et_otp_5 = findViewById(R.id.et_otp_5);
        et_otp_6 = findViewById(R.id.et_otp_6);

        btn_next = findViewById(R.id.btn_next);

        ll_resend = findViewById(R.id.ll_resend);
        ll_timer = findViewById(R.id.ll_timer);

        tv_resend_code = findViewById(R.id.tv_resend_code);
        tv_time = findViewById(R.id.tv_time);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (et_otp_1.getText().toString().isEmpty()) {
                    ShowError();
                } else if (et_otp_2.getText().toString().isEmpty()) {
                    ShowError();
                } else if (et_otp_3.getText().toString().isEmpty()) {
                    ShowError();
                } else if (et_otp_4.getText().toString().isEmpty()) {
                    ShowError();
                } else if (et_otp_5.getText().toString().isEmpty()) {
                    ShowError();
                } else if (et_otp_6.getText().toString().isEmpty()) {
                    ShowError();
                } else {
                    code = et_otp_1.getText().toString() +
                            et_otp_2.getText().toString() +
                            et_otp_3.getText().toString() +
                            et_otp_4.getText().toString() +
                            et_otp_5.getText().toString() +
                            et_otp_6.getText().toString();
                    verifyOTPParams.put(Constants.CODE, code);
                    if (!TextUtils.isEmpty(code) || !TextUtils.isEmpty(mVerificationId))
                        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, code));
                    //verifyOTP();
                }
                break;
            case R.id.tv_resend_code:
                recreate();
               /* if (connected)
                else
                    Toast.makeText(mContext, "Please Connect to Internet!", Toast.LENGTH_SHORT).show();
                break;*/
        }
    }

    private void resendCode() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: " + sessionManager.getAccessToken());
        callbackStatusCall = api.resendOTP(sessionManager.getAccessToken());
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseResendCode = response.body();
                if (responseResendCode != null) {
                    customLoader.hideIndicator();
                    if (responseResendCode.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseResendCode.getMessage());
                        Toast.makeText(mContext, responseResendCode.getMessage(), Toast.LENGTH_SHORT).show();
                        startCountDown();
                    } else {
                        Log.d(TAG, "onResponse: " + responseResendCode.getMessage());
                        Toast.makeText(mContext, responseResendCode.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
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

    public class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.et_otp_1:
                    if (text.length() == 1)
                        et_otp_2.requestFocus();
                    break;
                case R.id.et_otp_2:
                    if (text.length() == 1)
                        et_otp_3.requestFocus();
                    else if (text.length() == 0)
                        et_otp_1.requestFocus();
                    break;
                case R.id.et_otp_3:
                    if (text.length() == 1)
                        et_otp_4.requestFocus();
                    else if (text.length() == 0)
                        et_otp_2.requestFocus();
                    break;
                case R.id.et_otp_4:
                    if (text.length() == 1)
                        et_otp_5.requestFocus();
                    else if (text.length() == 0)
                        et_otp_3.requestFocus();
                    break;
                case R.id.et_otp_5:
                    if (text.length() == 1)
                        et_otp_6.requestFocus();
                    else if (text.length() == 0)
                        et_otp_4.requestFocus();
                    break;
                case R.id.et_otp_6:
                    if (text.length() == 0)
                        et_otp_5.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    public void verifyOTP() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: " + code);
        callbackVerifyOTPCall = api.verifyOTP(sessionManager.getAccessToken(), verifyOTPParams);
        callbackVerifyOTPCall.enqueue(new Callback<CallbackVerifyOTP>() {
            @Override
            public void onResponse(Call<CallbackVerifyOTP> call, Response<CallbackVerifyOTP> response) {
                customLoader.hideIndicator();
                Log.d(TAG, "onResponse: " + response);
                responseVerifyOTP = response.body();
                if (responseVerifyOTP != null) {
                    if (responseVerifyOTP.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseVerifyOTP.getMessage());
                        String email = responseVerifyOTP.getData().getUser().getEmail();

                        Toast.makeText(mContext, responseVerifyOTP.getMessage(), Toast.LENGTH_SHORT).show();
                        if (email != null) {
                            sessionManager.saveUserEmail(responseVerifyOTP.getData().getUser().getEmail());
                            sessionManager.saveUserName(responseVerifyOTP.getData().getUser().getName());
                            sessionManager.saveBirthday(responseVerifyOTP.getData().getUser().getBirthday());
                            sessionManager.saveUserID(responseVerifyOTP.getData().getUser().getId().toString());
                            sessionManager.createLoginSession("Bearer " + responseVerifyOTP.getData().getUser().getAuthToken(), responseVerifyOTP.getData().getUser().getId().toString());
                            Toast.makeText(mContext, responseVerifyOTP.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(mContext, MainActivity.class);
                            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
                            verifyOTPParams.clear();
                        } else {
                            startActivity(new Intent(mContext, LocationActivity.class));
                            finish();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseVerifyOTP.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseVerifyOTP.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackVerifyOTP> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());

                }
            }
        });
    }

    private void ShowError() {
        Toast.makeText(mContext, "Please enter complete OTP", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            initiateAuth(phoneNumber);
            //we are connected to a network
            connected = true;
        } else
            Toast.makeText(this, "Please Connect to Internet!", Toast.LENGTH_SHORT).show();
        connected = false;
    }

    private void initiateAuth(String phone) {
        customLoader.showIndicator();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        customLoader.hideIndicator();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onVerificationFailed: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        mVerificationId = verificationId;
                        customLoader.hideIndicator();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        customLoader.showIndicator();
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                customLoader.hideIndicator();
                initUserService();
                if (task.isSuccessful()) {
                    AppState.currentFireUser = mAuth.getCurrentUser();
                    FirebaseUserModel model = userService.getUserById(AppState.currentFireUser.getUid());
                    AppState.currentBpackCustomer = model;

                    if (model == null) {
                        createUser(AppState.currentFireUser);
                    } else {
                        sessionManager.saveFirebaseId(AppState.currentFireUser.getUid());
                        sessionManager.saveUserPhoneNo(phoneNumber);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/order_" + AppState.currentFireUser.getUid());
                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/order_" + AppState.currentFireUser.getUid());
                        sendOTPParams.put(Constants.PHONE, phoneNumber);

                        continueWithPhone();
                    }

                } else {
//                    Log.d("messagee", "onComplete: " + task.getResult());
                    // If sign in fails, display a message to the user.
                    Toast.makeText(mContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    customLoader.hideIndicator();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Verification Fail!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(PhoneVerificationForUser.this, String.format(getString(R.string.error_detail), e.getMessage()) != null ? "\n" + e.getMessage() : "", Toast.LENGTH_LONG).show();
                customLoader.hideIndicator();
            }
        });
    }

    private void continueWithPhone() {
        //customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: " + code);
        sendOTPParams.put("device_type", "android");
        callbackContinueWithPhoneCall = api.continueWithPhone(sendOTPParams);
        callbackContinueWithPhoneCall.enqueue(new Callback<CallbackContinueWithPhone>() {
            @Override
            public void onResponse(Call<CallbackContinueWithPhone> call, Response<CallbackContinueWithPhone> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                responseContinueWithPhone = response.body();
                if (responseContinueWithPhone != null) {
                    if (responseContinueWithPhone.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseContinueWithPhone.getMessage());
                        String email = responseContinueWithPhone.getUser().getEmail();
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseContinueWithPhone.getMessage(), Toast.LENGTH_SHORT).show();
                        if (email != null) {
                            sessionManager.saveUserEmail(responseContinueWithPhone.getUser().getEmail());
                            sessionManager.saveUserName(responseContinueWithPhone.getUser().getName());
                            sessionManager.saveBirthday(responseContinueWithPhone.getUser().getBirthday());
                            sessionManager.saveUserID(responseContinueWithPhone.getUser().getId().toString());
                            sessionManager.createLoginSession("Bearer " + responseContinueWithPhone.getUser().getAuthToken(), responseContinueWithPhone.getUser().getId().toString());
                            Toast.makeText(mContext, responseContinueWithPhone.getMessage(), Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(mContext, MainActivity.class);
                            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
                            sendOTPParams.clear();
                        } else {
                            sessionManager.createLoginSession("Bearer " + responseContinueWithPhone.getUser().getAuthToken(), responseContinueWithPhone.getUser().getId().toString());
                            startActivity(new Intent(mContext, LocationActivity.class));
                            finish();
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseContinueWithPhone.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseContinueWithPhone.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackContinueWithPhone> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void createUser(final FirebaseUser currentUser) {
        if (currentUser.getUid() != null) {
            userService.registerUser(currentUser.getUid(), phoneNumber, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        //customLoader.hideIndicator();
                        sessionManager.saveUserID(AppState.currentFireUser.getUid());
                        sessionManager.saveUserPhoneNo(phoneNumber);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/order_" + AppState.currentFireUser.getUid());
                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/order_" + AppState.currentFireUser.getUid());
                        sendOTPParams.put(Constants.PHONE, phoneNumber);
                        continueWithPhone();
                    } else {
                        customLoader.hideIndicator();
                        Log.d("messagee", "onComplete: " + databaseError.getMessage());
                        Toast.makeText(mContext, "SigUp failed - database error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(mContext, "SigUp failed, email empty. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUserService() {
        userService = new UserService();
        userService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}
