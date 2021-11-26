package com.jobesk.kikiiapp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Fragments.Others.MyFriendsFragment;
import com.jobesk.kikiiapp.Fragments.Others.PendingFragment;
import com.jobesk.kikiiapp.Fragments.Others.SentRequestsFragment;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;

public class FriendsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private FrameLayout tab_frame;


    private MyFriendsFragment myFriendsFragment;
    private PendingFragment pendingFragment;
    private SentRequestsFragment sentRequestsFragment;

    //    public static final String TAG_MY_FRIENDS = "tag_my_friends";
//    public static final String TAG_PENDING = "tag_pending";
//    public static final String TAG_SENT_REQUESTS = "tag_sent_requests";
//    public static final String CURRENT_TAG = TAG_MY_FRIENDS;
    public static int navItemIndex = 0;

//    private Handler mHandler;
    private ImageView img_back;
    private TextView my_friends_tv, pending_tv, sent_requests_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        myFriendsFragment = new MyFriendsFragment();
        pendingFragment = new PendingFragment();
        sentRequestsFragment = new SentRequestsFragment();


        initComponents();
        loadFragment(myFriendsFragment);


        img_back.setOnClickListener(this);
    }

    private void initComponents() {

//        mHandler = new Handler();
        myFriendsFragment = new MyFriendsFragment();
        pendingFragment = new PendingFragment();
        sentRequestsFragment = new SentRequestsFragment();
        tab_frame = findViewById(R.id.tab_frame);

        img_back = findViewById(R.id.img_back);


        my_friends_tv = findViewById(R.id.my_friends_tv);
        pending_tv = findViewById(R.id.pending_tv);
        sent_requests_tv = findViewById(R.id.sent_requests_tv);

        my_friends_tv.setOnClickListener(this);
        pending_tv.setOnClickListener(this);
        sent_requests_tv.setOnClickListener(this);

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_friends_tv:
                selectMyFriends();
                break;
            case R.id.pending_tv:
                selectPending();
                break;
            case R.id.sent_requests_tv:
                selectSentRequests();
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }

    private void selectSentRequests() {

        my_friends_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));
        pending_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));
        sent_requests_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));

        navItemIndex = 2;
        loadFragment(sentRequestsFragment);
    }

    private void selectPending() {

        my_friends_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));
        pending_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        sent_requests_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));


        navItemIndex = 1;
        loadFragment(pendingFragment);
    }

    private void selectMyFriends() {

        my_friends_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        pending_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));
        sent_requests_tv.setTextColor(getApplicationContext().getResources().getColor(R.color.grey));

        navItemIndex = 0;


        loadFragment(myFriendsFragment);
    }

    private void loadFragment(Fragment newFrag) {
//        Runnable mPendingRunnable = new Runnable() {
//            @Override
//            public void run() {
        Fragment fragment = new Fragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                /*fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);*/
        fragmentTransaction.replace(R.id.tab_frame, newFrag);
        //  fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commitAllowingStateLoss();
        fragmentTransaction.commit();
//            }
//        };
//
//        if (mPendingRunnable != null) {
//            mHandler.post(mPendingRunnable);
//        }
    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 0:
                return myFriendsFragment;
            case 1:
                return pendingFragment;
            case 2:
                return sentRequestsFragment;
            default:
                return new MyFriendsFragment();
        }
    }
}
