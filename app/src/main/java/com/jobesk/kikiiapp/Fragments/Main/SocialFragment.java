package com.jobesk.kikiiapp.Fragments.Main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Activities.CreatePostActivity;
import com.jobesk.kikiiapp.Activities.EventDetailActivity;
import com.jobesk.kikiiapp.Activities.KikiiInfoActivity;
import com.jobesk.kikiiapp.Fragments.Others.CommunityFragment;
import com.jobesk.kikiiapp.Fragments.Others.EventsFragment;
import com.jobesk.kikiiapp.Fragments.Others.KikiiFragment;
import com.jobesk.kikiiapp.R;

public class SocialFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "SocialFragment";
    private Context context;
    private FrameLayout tab_frame;
    private ImageView

            img_kikii_info;
    public static final int REQUEST_CREATE_POST = 256;
    private CommunityFragment communityFragment;
    private EventsFragment eventsFragment;
    private KikiiFragment kikiiFragment;

    public static final String TAG_COMMUNITY = "tag_community";
    public static final String TAG_EVENTS = "tag_events";
    public static final String TAG_KIKII = "tag_kikii";
    public static String CURRENT_TAG = TAG_COMMUNITY;
    public static int navItemIndex = 0;
    private ImageView img_add;
    private Handler mHandler;

    private TextView commuity_tv, events_tv, kiki_tv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        initComponents(view);
        loadFragment();


        return view;
    }


    private void initComponents(View view) {
        context = getContext();
        mHandler = new Handler();

        communityFragment = new CommunityFragment();
        eventsFragment = new EventsFragment();
        kikiiFragment = new KikiiFragment();

        tab_frame = view.findViewById(R.id.tab_frame);


        img_add = view.findViewById(R.id.img_add);
        img_add.setOnClickListener(this);

        img_kikii_info = view.findViewById(R.id.img_kikii_info);

        img_kikii_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, KikiiInfoActivity.class));
            }
        });
        commuity_tv = view.findViewById(R.id.commuity_tv);
        events_tv = view.findViewById(R.id.events_tv);
        kiki_tv = view.findViewById(R.id.kiki_tv);

        commuity_tv.setOnClickListener(this);
        events_tv.setOnClickListener(this);
        kiki_tv.setOnClickListener(this);


    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commuity_tv:
                selectCommunity();
                break;
            case R.id.events_tv:
                selectEvent();
                break;
            case R.id.kiki_tv:
                selectKikii();
                break;
            case R.id.img_add:
                if (navItemIndex == 0) {
                    startActivityForResult(new Intent(context, CreatePostActivity.class), REQUEST_CREATE_POST);
                } else {
                    startActivity(new Intent(context, EventDetailActivity.class));
                }
                break;

        }
    }

    private void selectKikii() {

        commuity_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));
        events_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));
        kiki_tv.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

        navItemIndex = 2;
        img_kikii_info.setVisibility(View.VISIBLE);
        img_add.setVisibility(View.GONE);
        loadFragment();
    }

    private void selectEvent() {

        commuity_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));
        events_tv.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        kiki_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));

        navItemIndex = 1;
        img_kikii_info.setVisibility(View.GONE);
        img_add.setVisibility(View.GONE);
        loadFragment();
    }

    private void selectCommunity() {
        commuity_tv.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        events_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));
        kiki_tv.setTextColor(getActivity().getResources().getColor(R.color.grey));

        navItemIndex = 0;
        img_kikii_info.setVisibility(View.GONE);
        img_add.setVisibility(View.VISIBLE);
        loadFragment();
    }

    private void loadFragment() {
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                /*fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);*/
                fragmentTransaction.replace(R.id.tab_frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    private Fragment getFragment() {
        switch (navItemIndex) {
            case 0:
                CURRENT_TAG = TAG_COMMUNITY;
                return communityFragment;
            case 1:
                CURRENT_TAG = TAG_EVENTS;
                return eventsFragment;
            case 2:
                CURRENT_TAG = TAG_KIKII;
                return kikiiFragment;
            default:
                CURRENT_TAG = TAG_COMMUNITY;
                return new CommunityFragment();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CREATE_POST && resultCode == Activity.RESULT_OK) {

//            loadFragment();

        }
    }


}
