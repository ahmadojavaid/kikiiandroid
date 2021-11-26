package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jobesk.kikiiapp.Activities.MainActivity;
import com.jobesk.kikiiapp.Callbacks.CallbackFacebookLogin;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramFields;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramLogin;
import com.jobesk.kikiiapp.Callbacks.CallbackInstagramOAuth;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity";
    public static final int REQUEST_LOGIN_WITH_INSTA = 100;
    private Context mContext = SignUpActivity.this;
    private Button btn_cont_with_phone;
    private LoginButton btn_fb_login;
    private LinearLayout ll_cont_with_fb, ll_cont_with_insta;
    private CallbackManager callbackManager;

    private String socialName, socialId, socialEmail, socialUserBirthday, instaCode;

    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackFacebookLogin> callbackFacebookLoginCall;
    private CallbackFacebookLogin responseFacebookLogin;

    private Call<CallbackInstagramLogin> callbackInstagramLoginCall;
    private CallbackInstagramLogin responseInstagramLogin;

    private Call<CallbackInstagramOAuth> callbackInstagramOAuthCall;
    private CallbackInstagramOAuth responseInstagramOAuth;

    private Call<CallbackInstagramFields> callbackInstagramFieldsCall;
    private CallbackInstagramFields responseInstagramFields;

    private Map<String, String> fbLoginParam = new HashMap<>();
    private Map<String, String> instaLoginParam = new HashMap<>();
    private Map<String, String> instaAccessTokenParams = new HashMap<>();
    private Map<String, String> instaFieldsParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initComponents();

        btn_cont_with_phone.setOnClickListener(this);
        ll_cont_with_fb.setOnClickListener(this);
        ll_cont_with_insta.setOnClickListener(this);
    }


    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        callbackManager = CallbackManager.Factory.create();

        btn_cont_with_phone = findViewById(R.id.btn_cont_with_phone);

        btn_fb_login = findViewById(R.id.fb_login_button);
        ArrayList<String> permission = new ArrayList<String>();
        permission.add("email");
        permission.add("public_profile");
        permission.add("user_birthday");
        btn_fb_login.setReadPermissions(permission);

        ll_cont_with_fb = findViewById(R.id.ll_cont_with_fb);
        ll_cont_with_insta = findViewById(R.id.ll_cont_with_insta);

        /***FACEBOOK LOGIN CALLBACK***/
        btn_fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cont_with_phone:
                startActivity(new Intent(mContext, EnterPhoneNumberActivity.class));
                break;
            case R.id.ll_cont_with_fb:
                btn_fb_login.performClick();
                break;
            case R.id.ll_cont_with_insta:
                continueWithInsta();
                break;
        }
    }

            private void continueWithInsta() {
                Intent intent1 = new Intent(mContext, InstagramLoginWebView.class);
                Log.d(TAG, Constants.INSTAGRAM_LOGIN_LINK);
                intent1.putExtra(Constants.URL, Constants.INSTAGRAM_LOGIN_LINK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent1, REQUEST_LOGIN_WITH_INSTA);
            }

            AccessTokenTracker tokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                    if (currentAccessToken == null) {
                        Toast.makeText(mContext, "User Is logged out", Toast.LENGTH_SHORT).show();
                    } else {
                        loginUserProfile(currentAccessToken);
                    }
                }
            };

            private void loginUserProfile(AccessToken accessToken) {
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d(TAG, "onCompleted: here");
                    socialId = object.getString("id");
                    socialEmail = object.getString("email");
                    socialName = object.getString("name");
                    socialUserBirthday = object.optString("user_birthday");

                    if (socialUserBirthday == null || socialUserBirthday.equalsIgnoreCase(""))
                        socialUserBirthday = "N/A";
                    Log.d("siiiii", "onCompleted: Name:" + socialName
                            + "Email:" + socialEmail
                            + "UID:" + socialId
                            + "BIRTHDAY:" + socialUserBirthday);

                    if (socialId != null) {
                        fbLoginParam.put(Constants.EMAIL, socialEmail);
                        fbLoginParam.put(Constants.NAME, socialName);
                        fbLoginParam.put(Constants.UID, socialId);
                        fbLoginParam.put(Constants.BIRTHDAY, socialUserBirthday);
                        loginWithFacebook();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onCompleted: " + e.getMessage());
                }
            }
        });
        Bundle parameter = new Bundle();
        parameter.putString("fields", "id,name,email,birthday");
        //parameter.putString("fields", "first_name, last_name, email, id,user_birthday");
        request.setParameters(parameter);
        request.executeAsync();
    }

    public void loginWithFacebook() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "loginWithFacebook: " + fbLoginParam.size());
        callbackFacebookLoginCall = api.facebookLogin(fbLoginParam);
        callbackFacebookLoginCall.enqueue(new Callback<CallbackFacebookLogin>() {
            @Override
            public void onResponse(Call<CallbackFacebookLogin> call, Response<CallbackFacebookLogin> response) {
                Log.d(TAG, "onResponse: " + response);
                responseFacebookLogin = response.body();
                if (responseFacebookLogin != null) {
                    if (responseFacebookLogin.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseFacebookLogin.getMessage());
                        goToNextActivity();
                    } else {
                        Log.d(TAG, "onResponse: " + responseFacebookLogin.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseFacebookLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackFacebookLogin> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void goToNextActivity() {
        Double latitude = responseFacebookLogin.getUser().getLatitude();
        sessionManager.saveAccessToken("Bearer " + responseFacebookLogin.getUser().getAuthToken());
        sessionManager.isFbLogin(true);
        customLoader.hideIndicator();
        Toast.makeText(mContext, responseFacebookLogin.getMessage(), Toast.LENGTH_SHORT).show();
        if (latitude != null) {
            sessionManager.saveUserEmail(responseFacebookLogin.getUser().getEmail());
            sessionManager.saveUserName(responseFacebookLogin.getUser().getName());
            sessionManager.saveBirthday(responseFacebookLogin.getUser().getBirthday());
            sessionManager.saveUserID(responseFacebookLogin.getUser().getId().toString());
            sessionManager.createLoginSession("Bearer " + responseFacebookLogin.getUser().getAuthToken(), responseFacebookLogin.getUser().getId().toString());
            Log.d(TAG, "onResponse: Bearer " + responseFacebookLogin.getUser().getAuthToken());
            Toast.makeText(mContext, responseFacebookLogin.getMessage(), Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(mContext, MainActivity.class);
            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
            fbLoginParam.clear();
        } else {
            startActivity(new Intent(mContext, LocationActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN_WITH_INSTA && resultCode == RESULT_OK) {
            if (data != null) {
                instaCode=data.getStringExtra(Constants.TOKEN);
                Log.d("aaa", "onActivityResult: "+instaCode);
                instaAccessTokenParams.put(Constants.CLIENT_ID,"940190363169482");
                instaAccessTokenParams.put(Constants.CLIENT_SECRET,"63de4d81dc0507154e54367bbc08626b");
                instaAccessTokenParams.put(Constants.CODE,instaCode);
                instaAccessTokenParams.put(Constants.GRANT_TYPE,"authorization_code");
                instaAccessTokenParams.put(Constants.REDIRECT_URI,"https://www.instagram.com/");
                getInstagramAccessToken(instaCode);
            }
        }
    }
    public void getInstagramAccessToken(String code) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext,"");
        Log.d(TAG, "loginWithFacebook: " + instaAccessTokenParams.size());
        callbackInstagramOAuthCall = api.instagramAccessToken(instaAccessTokenParams);
        callbackInstagramOAuthCall.enqueue(new Callback<CallbackInstagramOAuth>() {
            @Override
            public void onResponse(Call<CallbackInstagramOAuth> call, Response<CallbackInstagramOAuth> response) {
                Log.d(TAG, "onResponse: " + response);
                responseInstagramOAuth = response.body();
                if (responseInstagramOAuth != null) {
                    if (responseInstagramOAuth.getAccessToken()!=null) {
                        getInstagramFields(responseInstagramOAuth.getAccessToken());
                    } else {
                        customLoader.hideIndicator();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackInstagramOAuth> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }
    public void getInstagramFields(String accessToken) {
        API api = RestAdapter.createAPI(mContext,"fields");
        Log.d(TAG, "loginWithFacebook: " + fbLoginParam.size());
        callbackInstagramFieldsCall = api.instagramGetFields("id,username",accessToken);
        callbackInstagramFieldsCall.enqueue(new Callback<CallbackInstagramFields>() {
            @Override
            public void onResponse(Call<CallbackInstagramFields> call, Response<CallbackInstagramFields> response) {
                Log.d(TAG, "onResponse: " + response);
                responseInstagramFields = response.body();
                if (responseInstagramFields != null) {
                    if (responseInstagramFields.getId()!=null) {
                        Log.d(TAG, "onResponse: " + responseInstagramFields.getId());
                        instaLoginParam.put(Constants.EMAIL, "");
                        instaLoginParam.put(Constants.NAME, responseInstagramFields.getUsername());
                        instaLoginParam.put(Constants.UID, responseInstagramFields.getId());
                        loginWithInstagram();
                    } else {
                        Log.d(TAG, "onResponse: " + responseInstagramFields.getId());
                        customLoader.hideIndicator();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackInstagramFields> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }
    public void loginWithInstagram() {
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "loginWithInstagram: " + instaLoginParam.size());
        callbackInstagramLoginCall = api.instagramLogin(instaLoginParam);
        callbackInstagramLoginCall.enqueue(new Callback<CallbackInstagramLogin>() {
            @Override
            public void onResponse(Call<CallbackInstagramLogin> call, Response<CallbackInstagramLogin> response) {
                Log.d(TAG, "onResponse: " + response);
                responseInstagramLogin = response.body();
                if (responseInstagramLogin != null) {
                    if (responseInstagramLogin.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseInstagramLogin.getMessage());
                        goToNextActivityFromInstaLogin();
                    } else {
                        Log.d(TAG, "onResponse: " + responseInstagramLogin.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseInstagramLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackInstagramLogin> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void goToNextActivityFromInstaLogin() {
        Double latitude = responseInstagramLogin.getUser().getLatitude();
        sessionManager.saveAccessToken("Bearer " + responseInstagramLogin.getUser().getAuthToken());
        sessionManager.isInstaLogin(true);
        customLoader.hideIndicator();
        Toast.makeText(mContext, responseInstagramLogin.getMessage(), Toast.LENGTH_SHORT).show();
        if (latitude != null) {
            sessionManager.saveUserName(responseInstagramLogin.getUser().getName());
            sessionManager.saveUserID(responseInstagramLogin.getUser().getId().toString());
            sessionManager.createLoginSession("Bearer " + responseInstagramLogin.getUser().getAuthToken(), responseInstagramLogin.getUser().getId().toString());
            Log.d(TAG, "onResponse: Bearer " + responseInstagramLogin.getUser().getAuthToken());
            Toast.makeText(mContext, responseInstagramLogin.getMessage(), Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(mContext, MainActivity.class);
            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
            instaLoginParam.clear();
        } else {
            startActivity(new Intent(mContext, LocationActivity.class));
            finish();
        }
    }
}
