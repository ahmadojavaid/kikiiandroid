package com.jobesk.kikiiapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.jobesk.kikiiapp.Adapters.ImageAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Model.AdsImagesModel.AdsModel;
import com.jobesk.kikiiapp.Model.AdsImagesModel.Image;
import com.jobesk.kikiiapp.Model.CompleteUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jobesk.kikiiapp.Events.CheckInternetEvent;
import com.jobesk.kikiiapp.Fragments.Main.ChatFragment;
import com.jobesk.kikiiapp.Fragments.Main.MatchFragment;
import com.jobesk.kikiiapp.Fragments.Main.MeetFragment;
import com.jobesk.kikiiapp.Fragments.Main.NotificationFragment;
import com.jobesk.kikiiapp.Fragments.Main.SocialFragment;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CheckConnectivity;
import com.jobesk.kikiiapp.Utils.SaveArrayListAds;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MainActivity";
    private Context mContext = MainActivity.this;
    BottomNavigationView bottomNavigationView;
    FrameLayout main_frame;

    MeetFragment meetFragment;
    MatchFragment matchFragment;
    ChatFragment chatFragment;
    SocialFragment socialFragment;
    NotificationFragment notificationFragment;

    private BroadcastReceiver mNetworkReceiver;
    private FragmentManager fm = null;
//    private static NoNet mNoNet;

    public static final String TAG_MEET = "tag_meet";
    public static final String TAG_MATCH = "tag_match";
    public static final String TAG_CHAT = "tag_chat";
    public static final String TAG_SOCIAL = "tag_social";
    public static final String TAG_NOTIFICATION = "tag_notification";
    public static String CURRENT_TAG = TAG_MEET;
    public static int navItemIndex = 0;
    private Handler mHandler;
    private SessionManager sessionManager;
    private EventBus eventBus = EventBus.getDefault();

    int count = 0;
    private boolean shouldLoadHomeFragOnBackPress = true;

    View parentLayout;
    String extras;

    /******/
    private GoogleApiClient googleApiClient;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Call<CallbackUpdateProfile> callbackStatusCall;
    private CallbackUpdateProfile responseLatLongUpdate;
    private Map<String, String> updateLocationParams = new HashMap<>();
    private Double latitude, longitude;
    private String post_id, event_id, user_id;

    private List<Image> arrayListAds = new ArrayList<>();
    int currentPage = 0;
    ViewPager viewPager;
    LinearLayout pagerCon;

    /*****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // startActivity(new Intent(this, Billing.class));

        sessionManager = new SessionManager(this);
        String token = sessionManager.getAccessToken();
        Log.d("token", "onCreate: " + token);
        getUserProfile();

        setUpGClient();
        checkPermissions();
        iniComponents();
        loadHomeFragment();
        setUpNavigationView();

        /******/
        OnlineUser();
        /******/


        checkForDeepLink();
        getAddImages();

        if (getIntent().getStringExtra("type") == null) {

        } else {
            String notiType = getIntent().getStringExtra("type");


            if (notiType.equalsIgnoreCase("match")) {
                String image = getIntent().getStringExtra("otherUserImage");
                String id = getIntent().getStringExtra("otherUserID");
                Intent intent = new Intent(this, MatchDoneActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("otherUserImage", image);
                intent.putExtra("otherUserID", id);
                startActivity(intent);
            }


            if (notiType.equalsIgnoreCase("post_like")) {

                String postID = getIntent().getStringExtra("postID");
                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }

            if (notiType.equalsIgnoreCase("post_comment")) {

                String postID = getIntent().getStringExtra("postID");
                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }

            if (notiType.equalsIgnoreCase("comment_reply")) {

                String postID = getIntent().getStringExtra("postID");
                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("postID", postID);
                startActivity(intent);
            }


        }


        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(MainActivity.this);
        lbm.registerReceiver(receiver, new IntentFilter("go_to_match"));

    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            navItemIndex = 1;
            SocialFragment.navItemIndex = 0;
            CURRENT_TAG = TAG_MATCH;

//            if (menuItem.isChecked()) {
//                menuItem.setChecked(false);
//            } else {
//                menuItem.setChecked(true);
//            }
//            menuItem.setChecked(true);
            loadHomeFragment();
        }
    };

    private void initPagerAds() {

        viewPager = findViewById(R.id.view_pager);
        pagerCon = findViewById(R.id.pagerCon);

        if (arrayListAds.size() <= 0) {
            pagerCon.setVisibility(View.GONE);
            return;
        }


        ImageAdapter adapter = new ImageAdapter(getApplicationContext(), arrayListAds);
        viewPager.setAdapter(adapter);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == arrayListAds.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        int timeSwipe = getResources().getInteger(R.integer.ad_swipe_time);

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, timeSwipe, timeSwipe);

        // Pager listener over indicator
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

    private void getAddImages() {

        API api = RestAdapter.createAPI(MainActivity.this);
        Call callback = api.getAddImages();
        callback.enqueue(new Callback<AdsModel>() {
            @Override
            public void onResponse(Call<AdsModel> call, Response<AdsModel> response) {
                Log.d(TAG, "onResponse: " + response);
//                CallbackGetProfile responseGetProfile = response.body();
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: success");


                    arrayListAds = response.body().getImages();
                    SaveArrayListAds adsObj = new SaveArrayListAds();
                    adsObj.setImageList(arrayListAds);
                    initPagerAds();
                }
            }

            @Override
            public void onFailure(Call<AdsModel> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });
    }

    private void updaateFirebaseToken(String authToken) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FBToken", token);
        Map<String, String> updateProfileParams = new HashMap<>();
        updateProfileParams.put("device_token", token);
        updateProfileParams.put("device_type", "android");
        API api = RestAdapter.createAPI(MainActivity.this);
        Call callbackGetProfileCall = api.updateFirebaseToken("Bearer " + authToken, updateProfileParams);
        callbackGetProfileCall.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                Log.d(TAG, "onResponse: " + response);
//                CallbackGetProfile responseGetProfile = response.body();
                if (response.isSuccessful()) {

                    Log.d(TAG, "onResponse: success");
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });


    }

    private void getUserProfile() {
        String userID = sessionManager.getUserID();
        API api = RestAdapter.createAPI(MainActivity.this);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        Call callbackGetProfileCall = api.getProfile(sessionManager.getAccessToken(), userID);
        callbackGetProfileCall.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                CallbackGetProfile responseGetProfile = response.body();
                if (responseGetProfile != null) {
                    if (responseGetProfile.getSuccess()) {
                        CallbackGetProfile model = response.body();
                        String id = String.valueOf(model.getUser().getId());
                        String name = model.getUser().getName();
                        String email = model.getUser().getEmail();
                        String profilePic = model.getUser().getProfilePic();
                        String getupGraded = model.getUser().getUpgraded().toString();
                        String authToken = model.getUser().getAuthToken().toString();

                        sessionManager.saveProfileUser(responseGetProfile.getUser());
                        sessionManager.saveUpgraded(Integer.valueOf(getupGraded));

                        if (getupGraded.equalsIgnoreCase("1")) {
                            sessionManager.saveIsPaid(true);
                            sessionManager.saveUpgraded(1);

                        } else {
                            sessionManager.saveIsPaid(false);
                            sessionManager.saveUpgraded(0);
                        }
                        updatedataToFireBase(id, name, email, profilePic);
                        updaateFirebaseToken(authToken);
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });
    }

    private void updatedataToFireBase(String id, String name, String email, String profilePic) {

//        String userID = sessionManager.getUserID();
//        String name = sessionManager.getUserName();
//        String email = sessionManager.getUserEmail();
//        String photo = sessionManager.getProfileUser().getProfilePic();


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/");
        DatabaseReference mDatabaseRef = database.getReference();

        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef.child("Users").child(currentuser).child("email").setValue(email);
        mDatabaseRef.child("Users").child(currentuser).child("id").setValue(id);
        mDatabaseRef.child("Users").child(currentuser).child("isOnline").setValue("true");
        mDatabaseRef.child("Users").child(currentuser).child("type").setValue("user");
        mDatabaseRef.child("Users").child(currentuser).child("userId").setValue(currentuser);
        mDatabaseRef.child("Users").child(currentuser).child("userName").setValue(name);
        mDatabaseRef.child("Users").child(currentuser).child("image").setValue(profilePic);

    }


//    public class ImageAdapter extends PagerAdapter {
//
//        Context context;
//
//        LayoutInflater mLayoutInflater;
//
//        ImageAdapter(Context context) {
//            this.context = context;
//            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        @Override
//        public int getCount() {
//            return arrayListAds.size();
//        }
//
////        @Override
////        public boolean isViewFromObject(View view, Object object) {
////            return view == ((LinearLayout) object);
////        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            View itemView = mLayoutInflater.inflate(R.layout.page_viewer_pics, container, false);
//
//            String imagePos = arrayListAds.get(position).getPath();
//            ImageView pic_img = itemView.findViewById(R.id.imageView);
//            Picasso.get().load(imagePos).fit().centerCrop().into(pic_img);
//
//            container.addView(itemView);
//
//            return itemView;
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//
//            ViewPager vp = (ViewPager) container;
//            View view = (View) object;
//            vp.removeView(view);
//
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//    }


    private void checkForDeepLink() {
        //////////// Receiving Deep Link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();


                            post_id = deepLink.getQueryParameter("post_id");
                            Log.d(TAG, "onSuccess: " + post_id);
                            if (post_id != null) {
                                Intent intent = new Intent(mContext, SinglePostDetailActivity.class);
                                intent.putExtra(Constants.ID, String.valueOf(post_id));
                                startActivity(intent);
                                return;
                            }

                            event_id = deepLink.getQueryParameter("event_id");
                            if (event_id != null) {
                                user_id = deepLink.getQueryParameter("user_id");
                                if (user_id != null) {
                                    Intent intent = new Intent(mContext, UserProfileActivity.class);
                                    intent.putExtra(Constants.ID, String.valueOf(user_id));
                                    startActivity(intent);
                                    return;
                                }
                            }

                            user_id = deepLink.getQueryParameter("user_id");
                            if (user_id != null) {
                                Intent intent = new Intent(mContext, UserProfileActivity.class);
                                intent.putExtra(Constants.ID, String.valueOf(user_id));
                                startActivity(intent);
                                return;
                            }

                            String event_idHere = deepLink.getQueryParameter("event_idHere");
                            if (event_idHere != null) {
                                Intent intent = new Intent(mContext, EventDetailActivity.class);
                                intent.putExtra("eventID", String.valueOf(event_idHere));
                                startActivity(intent);
                                return;
                            }


                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dynamic link tag", "getDynamicLink:onFailure", e);
                    }
                });

    }

    private void iniComponents() {
        extras = getIntent().getStringExtra("CURRENT_TAG");

        //checkIntentData();

        main_frame = findViewById(R.id.main_frame);
        mNetworkReceiver = new CheckConnectivity();
        fm = getSupportFragmentManager();
        mHandler = new Handler();
        eventBus.register(this);
        registerNetworkBroadcastForNougat();

        meetFragment = new MeetFragment();
        matchFragment = new MatchFragment();
        chatFragment = new ChatFragment();
        socialFragment = new SocialFragment();
        notificationFragment = new NotificationFragment();


        bottomNavigationView = findViewById(R.id.main_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_meet);
        parentLayout = findViewById(android.R.id.content);
    }

    /*private void checkIntentData() {
        if(extras != null ){
            if (extras.equals("tag_live")) {
                navItemIndex=2;
                CURRENT_TAG=TAG_LIVE;
            }
            else if (extras.equals("tag_settings")) {
                navItemIndex=4;
                CURRENT_TAG=TAG_SETTINGS;
            }
            else if (extras.equals("tag_search")) {
                navItemIndex=1;
                CURRENT_TAG=TAG_SEARCH;
            }
            else if (extras.equals("tag_alert")) {
                navItemIndex=3;
                CURRENT_TAG=TAG_ALERT;

            }
        }
        else{
            navItemIndex=0;
            CURRENT_TAG=TAG_HOME;
        }
    }*/

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        unregisterNetworkChanges();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
    }

    private void loadHomeFragment() {

        selectNavMenu();
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getMeetFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
               /* fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);*/
                fragmentTransaction.replace(R.id.main_frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        invalidateOptionsMenu();
    }

    private void selectNavMenu() {
        bottomNavigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment getMeetFragment() {
        switch (navItemIndex) {
            case 0:
                return meetFragment;
            case 1:
                return matchFragment;
            case 2:
                return chatFragment;
            case 3:
                return socialFragment;
            case 4:
                return notificationFragment;
            default:
                return new MeetFragment();
        }
    }

    private void setUpNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_nav_meet:
                        navItemIndex = 0;
                        SocialFragment.navItemIndex = 0;
                        CURRENT_TAG = TAG_MEET;
                        break;
                    case R.id.bottom_nav_match:
                        navItemIndex = 1;
                        SocialFragment.navItemIndex = 0;
                        CURRENT_TAG = TAG_MATCH;
                        break;
                    case R.id.bottom_nav_chat:
                        navItemIndex = 2;
                        SocialFragment.navItemIndex = 0;
                        CURRENT_TAG = TAG_CHAT;
                        break;
                    case R.id.bottom_nav_social:
                        navItemIndex = 3;
                        SocialFragment.navItemIndex = 0;
                        CURRENT_TAG = TAG_SOCIAL;
                        break;
                    case R.id.bottom_nav_notify:
                        navItemIndex = 4;
                        SocialFragment.navItemIndex = 0;
                        CURRENT_TAG = TAG_NOTIFICATION;
                        break;
                    default:
                        SocialFragment.navItemIndex = 0;
                        navItemIndex = 0;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_MEET;
                loadHomeFragment();
                return;
            }
        }
        String catfrag = sessionManager.getfragmentval("catfrag");
        if (catfrag.equals("10")) {
            sessionManager.setfragmentval("catfrag", "0");
            navItemIndex = 0;
            CURRENT_TAG = TAG_MEET;
            loadHomeFragment();
            return;
        }
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(R.string.close_warning)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    //    /**
//     * EVENTS
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(NotificationReceivedEvent event) {
//        if (event.isIS_NOTIFY()) {
//            Snackbar snackbar = Snackbar.make(parentLayout, event.getRemoteMessage().getNotification().getTitle(), Snackbar.LENGTH_LONG);
//            View customView = getLayoutInflater().inflate(R.layout.snackbar_custom_notify, null);
//            //snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
//            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
//            snackbarLayout.setPadding(0, 0, 0, 0);
//            TextView tv_subject = customView.findViewById(R.id.tv_subject);
//            TextView tv_body = customView.findViewById(R.id.tv_body);
//
//            tv_subject.setText(event.getRemoteMessage().getNotification().getTitle());
//            tv_body.setText(event.getRemoteMessage().getNotification().getBody());
//
//            snackbarLayout.addView(customView);
//            snackbar.show();
//        }
//    }
//
//    ;
//
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CheckInternetEvent event) {
        Log.d("SsS", "checkInternetAvailability: called");
        if (event.isIS_INTERNET_AVAILABLE()) {
            ShowDialogues.SHOW_SNACK_BAR(parentLayout, MainActivity.this, getString(R.string.snackbar_internet_available));
            Log.d("fffff", "onMessageEvent: " + CURRENT_TAG);
            switch (CURRENT_TAG) {
                case TAG_MEET:
                    /*HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                    homeFragment.loadUserProfileData();*/
                case TAG_MATCH:
                    /*AlertFragment alertFragment = (AlertFragment) getSupportFragmentManager().findFragmentByTag(TAG_ALERT);
                    alertFragment.getAllNotifications();*/
                case TAG_CHAT:
                   /* StartBroadcastFragment startBroadcastFragment = (StartBroadcastFragment) getSupportFragmentManager().findFragmentByTag(TAG_LIVE);
                    startBroadcastFragment.loadFollowingStreams();*/
                case TAG_SOCIAL:
                    /*SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS);
//                    settingsFragment.getSettingsData();*/
            }
        } else {
            ShowDialogues.SHOW_SNACK_BAR(parentLayout, MainActivity.this, getString(R.string.snackbar_check_internet));
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            //customLoader.showIndicator();
            getMyLocation();
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                    LocationRequest locationRequest = new LocationRequest();

                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, MainActivity.this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {

                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(mContext,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult((Activity) mContext,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }


                    });
                }
            }
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //customLoader.showIndicator();
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                }
                break;
        }
        int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mylocation = location;
            if (mylocation != null) {
                latitude = mylocation.getLatitude();
                longitude = mylocation.getLongitude();

                sessionManager.saveLat(String.valueOf(latitude));
                sessionManager.saveLng(String.valueOf(longitude));

                //updateLocation();
                Log.d("TAG", "onLocationChanged:LATITUDE " + latitude);
                Log.d("TAG", "onLocationChanged:LONGITUDE " + longitude);

                updateLocationParams.put(Constants.LATITUDE, sessionManager.getLat());
                updateLocationParams.put(Constants.LONGITUDE, sessionManager.getLng());
                updateLocation();
            }
        } catch (Exception e) {
            Log.d("TAG", "onLocationChanged: " + e.getMessage());
        }
    }

    public void updateLocation() {
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: " + updateLocationParams.size());
        Log.d(TAG, "token: " + sessionManager.getAccessToken());
        callbackStatusCall = api.updateProfile(sessionManager.getAccessToken(), updateLocationParams);
        callbackStatusCall.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseLatLongUpdate = response.body();
                if (responseLatLongUpdate != null) {
                    if (responseLatLongUpdate.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseLatLongUpdate.getMessage());

                        //customLoader.hideIndicator();
                        //goToNextActivity();
                    } else {
                        Log.d(TAG, "onResponse: " + responseLatLongUpdate.getMessage());
                        //customLoader.hideIndicator();
                        Toast.makeText(mContext, responseLatLongUpdate.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    //customLoader.hideIndicator();
                }
            }
        });
    }

    private void OnlineUser() {
        DatabaseReference mUsersDatabase = null;
        if (AppState.currentBpackCustomer != null) {
            mUsersDatabase = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/").getReference().child("Users")
                    .child(AppState.currentFireUser.getUid());

            DatabaseReference finalMUsersDatabase = mUsersDatabase;
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        finalMUsersDatabase.child("isOnline").onDisconnect()
                                .setValue("false");
                        finalMUsersDatabase.child("isOnline").setValue("true");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}

