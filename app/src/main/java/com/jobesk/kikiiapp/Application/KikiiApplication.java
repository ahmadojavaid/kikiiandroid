package com.jobesk.kikiiapp.Application;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;

import com.jobesk.kikiiapp.BuildConfig;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Firebase.ChangeEventListener;
import com.jobesk.kikiiapp.Firebase.Services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.calling.EngineEventListener;
import com.jobesk.kikiiapp.calling.Global;
import com.jobesk.kikiiapp.calling.IEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmCallManager;
import io.agora.rtm.RtmClient;


/**
 * Created by thanh on 09/12/2015.
 */
public class KikiiApplication extends Application {
    Context mContext;

    private static final String TAG = "KikiiApplication";
    public static KikiiApplication kikiiApplication;

    public static KikiiApplication getKikiiApplication() {
        return kikiiApplication;
    }

    public Locale mLocale;
    public SimpleDateFormat dayFormat;
    public SimpleDateFormat weatherDateStampFormat;

    private DatabaseReference mDatabase;
    public boolean changeSubCategoryComplete = false;
    private UserService userService;


    public RtmCallManager rtmCallManager;
    public EngineEventListener mEventListener;
    public RtcEngine mRtcEngine;
    public RtmClient mRtmClient;
    private Global mGlobal;

    public void onCreate() {
        super.onCreate();
        mContext = KikiiApplication.this;
        kikiiApplication = this;


        mDatabase = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/").getReference();


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            AppState.currentFireUser = FirebaseAuth.getInstance().getCurrentUser();
        }
//        .setPersistenceEnabled(true)

        FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/");
        userService = new UserService();
        userService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
                userService.isLoaded = true;
            }

            @Override
            public void onDataChanged() {
                userService.isLoaded = true;
                if (userService.getCount() > 0 && AppState.currentFireUser != null) {
                    AppState.currentBpackCustomer = userService.getUserById(AppState.currentFireUser.getUid());
                    if (AppState.currentBpackCustomer != null) {
                        Log.d(TAG, "USER FOUND: " + AppState.currentFireUser.getUid());
                    } else {
                        Log.d(TAG, "USER NOT FOUND: " + AppState.currentFireUser.getUid());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        mLocale = getResources().getConfiguration().locale;
        dayFormat = new SimpleDateFormat("EEE", mLocale);
        weatherDateStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", mLocale);

        initConfig();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        clearMemoryCache();
    }

    public void clearMemoryCache() {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        clearMemoryCache();
    }

    private void setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
    }


    private void initConfig() {
//        mConfig = new Config(getApplicationContext());
        mGlobal = new Global();
        initEngine();
    }

    private void initEngine() {
        String appId = getString(R.string.agora_app_id);
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
        }

        mEventListener = new EngineEventListener();
        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), appId, mEventListener);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableDualStreamMode(true);
            mRtcEngine.enableVideo();
//            mRtcEngine.setLogFile(FileUtil.rtmLogFile(getApplicationContext()));

            mRtmClient = RtmClient.createInstance(getApplicationContext(), appId, mEventListener);
//            mRtmClient.setLogFile(FileUtil.rtmLogFile(getApplicationContext()));

//            if (Config.DEBUG) {
//                mRtcEngine.setParameters("{\"rtc.log_filter\":65535}");
//                mRtmClient.setParameters("{\"rtm.log_filter\":65535}");
//            }

            rtmCallManager = mRtmClient.getRtmCallManager();
            rtmCallManager.setEventListener(mEventListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loginRTCclient(String token) {

        String accessToken = token;

        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "<#YOUR ACCESS TOKEN#>")) {
            accessToken = null;
        }
        mRtmClient.login(accessToken, "0", new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "rtm client login success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "rtm client login failed:" + errorInfo.getErrorDescription());
            }
        });

    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public RtmClient rtmClient() {
        return mRtmClient;
    }

    public void registerEventListener(IEventListener listener) {
        mEventListener.registerEventListener(listener);
    }

    public void removeEventListener(IEventListener listener) {
        mEventListener.removeEventListener(listener);
    }

    public RtmCallManager rtmCallManager() {
        return rtmCallManager;
    }

    //    public Config config() {
//        return mConfig;
//    }
//
    public Global global() {
        return mGlobal;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        destroyEngine();
    }

    private void destroyEngine() {
        RtcEngine.destroy();

        mRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "rtm client logout success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.i(TAG, "rtm client logout failed:" + errorInfo.getErrorDescription());
            }
        });
    }
}
