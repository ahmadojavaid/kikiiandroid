package com.jobesk.kikiiapp.Fragments.Main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Activities.FiltersActivity;
import com.jobesk.kikiiapp.Activities.MainActivity;
import com.jobesk.kikiiapp.Activities.MyProfileActivity;
import com.jobesk.kikiiapp.Activities.SignUpModule.SubscriptionActivityApp;

import com.jobesk.kikiiapp.Activities.SupportActivity;
import com.jobesk.kikiiapp.Adapters.MeetCardSwipeStackAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetMeetUsers;
import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Firebase.ChangeEventListener;
import com.jobesk.kikiiapp.Firebase.Services.UserService;
import com.jobesk.kikiiapp.Model.MeetUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, CardStackListener {

    private static final String TAG = "MeetFragment";
    private Context context;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<MeetUser> meetUsersList = new ArrayList<>();
    private MeetCardSwipeStackAdapter meetCardSwipeStackAdapter;
    private ImageView img_open_details, img_close_details, img_menu, img_filters, img_search;
    private LinearLayout ll_normal_view, ll_menu;
    private RelativeLayout rl_detail_view;
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackListener cardStackListener;
    private SwipeAnimationSetting swipeAnimationSetting;
    private boolean isMenuVisible = false;
    private TextView tv_profile, tv_support, tv_logout, tv_no;
    private ImageView img_main;
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetMeetUsers> callbackGetMeetUsersCall;
    private CallbackGetMeetUsers responseGetMeetUsers;

    private Call<CallbackStatus> callbackStatusCall;
    private CallbackStatus responseStatus;

    private Call<CallbackGetProfile> callbackGetProfileCall;
    private CallbackGetProfile responseGetProfile;
    private Map<String, String> paramsList = new HashMap<>();
    private ViewGroup parentLayout;
    private int currentCardPosition = 0;
    private PopupWindow mypopupWindow = null;
    private UserService userService;
    private Uri userProfileLink;

    boolean fromLike = false;
    boolean fromDislike = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meet, container, false);

//        activity = (MainActivity) getContext();

        initComponents(view);
        getUserProfile();

        img_menu.setOnClickListener(this);
        img_filters.setOnClickListener(this);
        img_search.setOnClickListener(this);
        tv_profile.setOnClickListener(this);
        tv_support.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        img_main.setOnClickListener(this);


        return view;
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
          //  Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "onReceive: ");
            getMeetUsers();
        }
    };


    private void getMeetUsers() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetMeetUsersCall = api.getMeetUsers(sessionManager.getAccessToken());
        callbackGetMeetUsersCall.enqueue(new Callback<CallbackGetMeetUsers>() {
            @Override
            public void onResponse(Call<CallbackGetMeetUsers> call, Response<CallbackGetMeetUsers> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                responseGetMeetUsers = response.body();
                if (responseGetMeetUsers != null) {
                    if (responseGetMeetUsers.getSuccess()) {
                        customLoader.hideIndicator();
                        updateFirebaseUser(AppState.currentFireUser);
                        //Toast.makeText(context, responseGetProfile.getMessage(), Toast.LENGTH_SHORT).show();
                        if (responseGetMeetUsers.getUsers().size() > 0) {
                            tv_no.setVisibility(View.GONE);
                            cardStackView.setVisibility(View.VISIBLE);
                            setData();
                        } else {
                            tv_no.setVisibility(View.VISIBLE);
                            cardStackView.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetMeetUsers.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetMeetUsers.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetMeetUsers> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {

        meetUsersList = responseGetMeetUsers.getUsers();

        setCardSwipe();
    }

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("refresh_for_block"));

        parentLayout = view.findViewById(android.R.id.content);
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);

        img_open_details = view.findViewById(R.id.img_open_details);
        img_close_details = view.findViewById(R.id.img_close_details);
        img_menu = view.findViewById(R.id.img_menu);
        img_filters = view.findViewById(R.id.img_filters);
        img_search = view.findViewById(R.id.img_search);

        ll_normal_view = view.findViewById(R.id.ll_normal_view);
        ll_menu = view.findViewById(R.id.ll_menu);

        rl_detail_view = view.findViewById(R.id.rl_detail_view);
        img_main = view.findViewById(R.id.img_main);

        cardStackView = view.findViewById(R.id.card_stack_view_frame);

        tv_profile = view.findViewById(R.id.tv_profile);
        tv_support = view.findViewById(R.id.tv_support);
        tv_logout = view.findViewById(R.id.tv_logout);
        tv_no = view.findViewById(R.id.tv_no);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        //tv_no.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(false);
        cardStackView.setVisibility(View.VISIBLE);

    }


    private void setCardSwipe() {

        cardStackLayoutManager = new CardStackLayoutManager(context, this);
        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setMaxDegree(20.0f);
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL);
        cardStackLayoutManager.setSwipeThreshold(0.5f);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setCanScrollVertical(false);
        cardStackView.setLayoutManager(cardStackLayoutManager);

        meetCardSwipeStackAdapter = new MeetCardSwipeStackAdapter(meetUsersList, context);
        cardStackView.setAdapter(meetCardSwipeStackAdapter);
        meetCardSwipeStackAdapter.setOnListEndListener(new MeetCardSwipeStackAdapter.IListEnd() {
            @Override
            public void onListEndListener() {
                tv_no.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);
                cardStackView.setVisibility(View.GONE);
            }
        });

        meetCardSwipeStackAdapter.setOnClickListener(new MeetCardSwipeStackAdapter.IClick() {
            @Override
            public void onLikeUserClick(MeetUser user, int position) {


                fromLike = true;
                fromDislike = false;

                likeUser(user);
                onCardSwiped(Direction.Right);
            }

            @Override
            public void onDislikeUserClick(MeetUser user, int position) {


                fromLike = false;
                fromDislike = true;
                dislikeUser(user);

                onCardSwiped(Direction.Left);

            }

            @Override
            public void onFollowUserClick(MeetUser user, int position) {

                fromLike = false;
                fromDislike = false;

                followUser(user);


            }

            @Override
            public void onBlockUserClick(MeetUser user, int position) {
                blockUser(user, position);
            }

            @Override
            public void onReportUserClick(MeetUser user, int position) {
                reportUser(user);
            }

            @Override
            public void onShareUserProfile(MeetUser user, int position) {
                createShareLink(user);
            }
        });


    }


    private void createShareLink(MeetUser user) {
        String packageName = activity.getPackageName();
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/?user_id=" + user.getId()))
                .setDynamicLinkDomain("kikiiapp1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
//                                .setMinimumVersion(125)
                                .build())
                // Open links with com.example.ios on iOS
                .buildDynamicLink();
        userProfileLink = dynamicLink.getUri();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(userProfileLink)
                .buildShortDynamicLink()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            customLoader.hideIndicator();
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.d(TAG, "onComplete: " + shortLink);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            startActivity(Intent.createChooser(intent, "Share"));
                        } else {
                            customLoader.hideIndicator();
                            Log.d(TAG, "ERROR: " + task.getException());
                        }
                    }
                });
    }

    private void followUser(MeetUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.followUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (!responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                    }
                    customLoader.hideIndicator();
                    getMeetUsers();
                    Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    private void blockUser(MeetUser user, int position) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.blockUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (!responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                    }
                    customLoader.hideIndicator();

                    blockUserAlert(activity, position);

//                    Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    public void blockUserAlert(Activity activity, int position) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_block_user, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                meetUsersList.remove(position);
                meetCardSwipeStackAdapter.notifyDataSetChanged();
                deleteDialog.dismiss();
            }
        };
        handler.postDelayed(runnable, 2000);

        deleteDialog.show();
    }


    private void reportUser(MeetUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackStatusCall = api.reportUser(user.getId().toString(), sessionManager.getAccessToken());
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (!responseStatus.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                    }
                    customLoader.hideIndicator();
                    showAlertReport(activity);
//                    Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    public static void showAlertReport(Activity activity) {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_report_success, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                deleteDialog.dismiss();
            }
        };
        handler.postDelayed(runnable, 2000);

        deleteDialog.show();
    }

    private void dislikeUser(MeetUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.dislikeUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (responseStatus.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();

                        getMeetUsers();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    private void likeUser(MeetUser user) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        paramsList.put(Constants.ID, user.getId().toString());
        callbackStatusCall = api.likeUser(sessionManager.getAccessToken(), paramsList);
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (responseStatus.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();

                        getMeetUsers();
                    } else {
                        Log.d(TAG, "onResponse: " + responseStatus.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseStatus.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: ");


        if (requestCode == 1) {


            if (meetUsersList.size() > 0) {
                meetUsersList.clear();
            }

            getMeetUsers();

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_menu:
                Log.d(TAG, "onClick: img_menu");
                showPostMenu(activity, img_menu, parentLayout);
                break;
            case R.id.img_filters:
                Intent intent = new Intent(context, FiltersActivity.class);
                startActivityForResult(intent, 1);

                break;
            case R.id.img_search:
                break;
            case R.id.tv_profile:
                closeMenu();
                startActivity(new Intent(context, MyProfileActivity.class));
                break;
            case R.id.tv_support:
                closeMenu();
                startActivity(new Intent(context, SupportActivity.class));
                break;
            case R.id.tv_logout:
                sessionManager.logoutUser();
                activity.finish();
            case R.id.img_main:
                closeMenu();
                break;
        }
    }

    private void openMenu() {
        isMenuVisible = true;
        ll_menu.setVisibility(View.VISIBLE);
    }

    private void closeMenu() {
        isMenuVisible = false;
        ll_menu.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {

        if (direction == Direction.Right) {
            if (currentCardPosition != -1) {
                if (fromLike == false) {
                    likeUser(meetUsersList.get(currentCardPosition));
                } else {
                    fromLike = false;
                }
            }
            return;
        }

        if (direction == Direction.Left) {
            if (currentCardPosition != -1) {
                if (fromDislike == false) {
                    dislikeUser(meetUsersList.get(currentCardPosition));
                } else {

                    fromDislike = false;

                }


            }
            return;
        }

//        if (direction == Direction.Bottom) {
//            if (currentCardPosition != -1) {
//
//                if (fromFollow == false) {
//                    followUser(meetUsersList.get(currentCardPosition));
//                } else {
//                    fromFollow = false;
//                }
//                return;
//            }
//
//        }
    }


    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
        currentCardPosition = position;
        Log.d(TAG, "onCardAppeared: " + currentCardPosition);
    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    private void getUserProfile() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d("getUserProfile", "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetProfileCall = api.getProfile(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetProfileCall.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response));
                responseGetProfile = response.body();
                if (responseGetProfile != null) {
                    if (responseGetProfile.getSuccess()) {
                        //customLoader.hideIndicator();
                        sessionManager.saveProfileUser(responseGetProfile.getUser());
                        getMeetUsers();
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetProfile.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    public void rewindSwipes() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackStatusCall = api.rewindSwipes(sessionManager.getAccessToken());
        callbackStatusCall.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseStatus = response.body();
                if (responseStatus != null) {
                    if (responseStatus.getSuccess()) {
                        getMeetUsers();
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetProfile.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
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

    public void showPostMenu(final Activity activity, View view, ViewGroup viewGroup) {
        sessionManager = new SessionManager(activity);

        LayoutInflater inflater = (LayoutInflater)
                activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.home_menu, viewGroup, false);

        TextView tv_profile = v.findViewById(R.id.tv_profile);
        TextView tv_incognito = v.findViewById(R.id.tv_incognito);
        TextView tv_rewind = v.findViewById(R.id.tv_rewind);
        TextView tv_support = v.findViewById(R.id.tv_support);
        TextView tv_logout = v.findViewById(R.id.tv_logout);

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        tv_incognito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                // onIncognitoMode(activity,params);
                //activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        tv_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();

                boolean isPaid = sessionManager.getIsPaid();
                if (isPaid) {
                    rewindSwipes();
                } else {

                    Intent intent = new Intent(activity, SubscriptionActivityApp.class);
                    intent.putExtra("from", "meetfrag");
                    startActivity(intent);

                }


            }
        });
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                activity.startActivity(new Intent(activity, SupportActivity.class));
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                sessionManager.logoutUser();
                activity.finish();

            }
        });
        mypopupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v); // <- fix
        }
        mypopupWindow.showAsDropDown(view, 0, 0);

        mypopupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
            }
        });
    }

    private void updateFirebaseUser(final FirebaseUser currentUser) {
//        String currentUserID = currentUser.getUid();


//        String currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        if ( currentuserID !=null && !currentuserID.isEmpty()){
//            initUserService();
//            userService.updateUserProfile(
//                    currentUser.getUid(),
//                    String.valueOf(sessionManager.getProfileUser().getId()),
//                    "user",
//                    sessionManager.getProfileUser().getName(),
//                    sessionManager.getProfileUser().getPhone(),
//                    sessionManager.getProfileUser().getEmail(),
//                    sessionManager.getProfileUser().getProfilePic(),
//                    new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError == null) {
//                                AppState.currentBpackCustomer = userService.getUserById(AppState.currentFireUser.getUid());
//                            } else {
//                                customLoader.hideIndicator();
//                                Log.d("messagee", "onComplete: " + databaseError.getMessage());
//                                Toast.makeT5120
//                                ext(context, "SigUp failed - database error", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//        } else{
//            Toast.makeText(context, "SigUp failed, email empty. Please try again", Toast.LENGTH_SHORT).show();
//        }
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
