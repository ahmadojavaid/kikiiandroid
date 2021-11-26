package com.jobesk.kikiiapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jobesk.kikiiapp.Adapters.EventAttendantsDetailAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Model.Attendant;
import com.jobesk.kikiiapp.Model.Event;
import com.jobesk.kikiiapp.Model.eventGetModel.GetEventModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "EventDetailActivity";
    private Context context = EventDetailActivity.this;

    private RecyclerView rv_attending;
    private EventAttendantsDetailAdapter eventAttendantsDetailAdapter;
    private List<Attendant> attendingUsersList = new ArrayList<>();
    //    private Event event;
    private SelectableRoundedImageView img_event;
    private TextView tv_title, tv_description, tv_time, tv_user_name;
    private ImageView img_join, img_share;
    private CircleImageView img_user;

    private Map<String, String> eventParams = new HashMap<>();
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackStatus> callbackAttendEvent;
    private CallbackStatus responseAttendEvent;
    private Uri userProfileLink;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        getIntentData();
        initComponents();


        img_join.setOnClickListener(this);
        getSingleEvent();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        eventID = intent.getStringExtra("eventID");
//        event = (Event) bundle.getSerializable("event");
    }

    public void getSingleEvent() {
        sessionManager = new SessionManager(getApplicationContext());

        API api = RestAdapter.createAPI(getApplicationContext());


        String token = sessionManager.getAccessToken();

        Call callbackStatusCall = api.getSingleEvent(token, eventID);
        callbackStatusCall.enqueue(new Callback<GetEventModel>() {
            @Override
            public void onResponse(Call<GetEventModel> call, Response<GetEventModel> response) {
                customLoader.hideIndicator();
                Log.d("hereGiveEvent", "onResponse: " + new Gson().toJson(response.body()));

                if (response.isSuccessful()) {


                    GetEventModel model = response.body();
                    Event event = model.getEvent();
                    setData(event);

                }


            }

            @Override
            public void onFailure(Call<GetEventModel> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());

                }
            }
        });
    }


    private void setData(Event event) {
        tv_title.setText(event.getName());
        tv_description.setText(event.getDescription());
        tv_time.setText(event.getDatetime());
        tv_user_name.setText(event.getUser().getName());

        Glide.with(context)
                .load(event.getUser().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_user);
        Glide.with(context)
                .load(event.getCoverPic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_event);

        attendingUsersList = event.getAttendants();
        rv_attending.setAdapter(new EventAttendantsDetailAdapter(attendingUsersList, this));

    }

    private void initComponents() {

        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(context);

        img_event = findViewById(R.id.img_event);
        img_join = findViewById(R.id.img_join);
        img_share = findViewById(R.id.img_share);
        img_user = findViewById(R.id.img_user);

        tv_title = findViewById(R.id.tv_title);
        tv_time = findViewById(R.id.tv_time);
        tv_description = findViewById(R.id.tv_description);
        tv_user_name = findViewById(R.id.tv_user_name);

        rv_attending = findViewById(R.id.rv_attending);
        rv_attending.setLayoutManager(new GridLayoutManager(this, 6));

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_share.setEnabled(false);
                createShareLink();

            }
        });
    }

    private void createShareLink() {

        String packageName = getApplicationContext().getPackageName();
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/?event_idHere=" + eventID))
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
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        img_share.setEnabled(true);
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

    private void attendEvent() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackAttendEvent = api.attendEvent(sessionManager.getAccessToken(), eventParams);
        callbackAttendEvent.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d("attendEventdetails", "onResponse: " + new Gson().toJson(response.body()));
                responseAttendEvent = response.body();
                if (responseAttendEvent != null) {
                    if (responseAttendEvent.getSuccess()) {
                        customLoader.hideIndicator();
                        showAlertJoined(EventDetailActivity.this);
//                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: " + responseAttendEvent.getMessage());
                        customLoader.hideIndicator();
                        showAlertEventLeft(EventDetailActivity.this);
//                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                        eventParams.clear();
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

    public static void showAlertJoined(Activity activity) {

        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_event_joined, null);
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

    public static void showAlertEventLeft(Activity activity) {

        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_event_un_joined, null);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_join:
//                eventParams.put(Constants.ID, event.getId().toString());
//                attendEvent();
                break;
        }
    }
}
