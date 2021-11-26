package com.jobesk.kikiiapp.calling;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Application.KikiiApplication;
import com.jobesk.kikiiapp.Callbacks.CallbackGetFilters;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CircleTransform;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VoiceChatViewActivity extends BaseCallActivity {

    private static final String LOG_TAG = VoiceChatViewActivity.class.getSimpleName();

    private String TAG = "VoiceChatViewActivity";

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    MediaPlayer player;
    private RtcEngine mRtcEngine; // Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        // Tutorial Step 1

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */

        @Override
        public void onUserOffline(final int uid, final int reason) {

            // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                    onBackPressed();
                }
            });
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            mRtcEngine.setEnableSpeakerphone(false);
            Log.d("joined", "onUserJoined: ");
            if (player.isPlaying()) {
                player.stop();
            }
            startStopChronometer();
            cancel_tv.setText("End Call");

        }

        @Override
        public void onLocalUserRegistered(int uid, String userAccount) {
            super.onLocalUserRegistered(uid, userAccount);

        }
    };


    String receiverName, receiverImage, senderName, senderImage;
    String channelName, channeltoken;
    ImageView userImage, imageAttend;
    TextView userName_tv;
    SessionManager sessionManager;
    TextView decline_tv, cancel_tv, accept_tv;
    ImageView decline_img, imageCancellCall;
    private Chronometer chronometer;
    private boolean isStart;
    CustomLoader customLoader;
    String senderID;

    String receiverID;
    @Override
    public KikiiApplication application() {
        return super.application();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat_view);

        customLoader = new CustomLoader(this, false);

        Intent intent = getIntent();
        channelName = intent.getStringExtra("channelName");
        channeltoken = intent.getStringExtra("channeltoken");

        userImage = findViewById(R.id.userImage);
        userName_tv = findViewById(R.id.userName_tv);

        decline_img = findViewById(R.id.decline_img);
        decline_tv = findViewById(R.id.decline_tv);
        cancel_tv = findViewById(R.id.cancel_tv);
        imageCancellCall = findViewById(R.id.imageCancellCall);
        imageAttend = findViewById(R.id.imageAttend);
        accept_tv = findViewById(R.id.accept_tv);

        receiverName = intent.getStringExtra("receiverName");
        receiverImage = intent.getStringExtra("receiverImage");
        senderName = intent.getStringExtra("senderName");
        senderImage = intent.getStringExtra("senderImage");
        receiverID = intent.getStringExtra("receiverID");
        senderID = intent.getStringExtra("sender_id");

        sessionManager = new SessionManager(VoiceChatViewActivity.this);
        // senderID = sessionManager.getUserID();

        if (senderName.equalsIgnoreCase(sessionManager.getUserName())) {

            application().loginRTCclient(channeltoken);
            //Other User Screen
            Picasso.get().load(receiverImage).fit().centerCrop().transform(new CircleTransform()).into(userImage);
            userName_tv.setText(receiverName);
            cancel_tv.setVisibility(View.VISIBLE);
            imageCancellCall.setVisibility(View.VISIBLE);
            decline_tv.setVisibility(View.GONE);
            decline_img.setVisibility(View.GONE);
            accept_tv.setVisibility(View.GONE);
            imageAttend.setVisibility(View.GONE);

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }

            startRinging(R.raw.basic_ring_sending);
            sendInvitation();


        } else {

            //Current User Screen
            Picasso.get().load(senderImage).fit().transform(new CircleTransform()).centerCrop().into(userImage);
            userName_tv.setText(senderName);
            cancel_tv.setVisibility(View.GONE);
            imageCancellCall.setVisibility(View.GONE);
            decline_tv.setVisibility(View.VISIBLE);
            decline_img.setVisibility(View.VISIBLE);
            accept_tv.setVisibility(View.VISIBLE);
            imageAttend.setVisibility(View.VISIBLE);
            startRinging(R.raw.basic_tones_receiving);

        }

        imageAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VisibilityResetButtons();
                imageCancellCall.setVisibility(View.VISIBLE);
                cancel_tv.setVisibility(View.VISIBLE);

                cancel_tv.setText("End Call");
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                    initAgoraEngineAndJoinChannel();
                }

            }
        });

        chronometer = findViewById(R.id.chronometer);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometerChanged) {
                chronometer = chronometerChanged;
            }
        });

        imageCancellCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendEndCallNotification(receiverID);

                Log.d(TAG, "onClick: ");

            }
        });

        decline_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEndCallNotification(senderID);
                Log.d(TAG, "onClick: ");
            }
        });

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(receiver, new IntentFilter("close_Call"));
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void sendEndCallNotification(String id) {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getApplicationContext());
        Log.d("TAG", "loadCommunityPosts: " + sessionManager.getAccessToken());
        Call<EndCallModel> callbackGetFiltersCall = api.endCallApi(sessionManager.getAccessToken(), id);
        callbackGetFiltersCall.enqueue(new Callback<EndCallModel>() {
            @Override
            public void onResponse(Call<EndCallModel> call, Response<EndCallModel> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                EndCallModel responseGetFilters = response.body();
                if (responseGetFilters != null) {
                    finish();
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<EndCallModel> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }


    private void sendInvitation() {
//        inviteCall(mPeer, mChannel);
        inviteCall("mPeer", channelName);
    }

    private MediaPlayer startRinging(int resource) {
        player = MediaPlayer.create(this, resource);
        player.setLooping(true);
        player.start();
        return player;
    }


    private void VisibilityResetButtons() {


        imageCancellCall.setVisibility(View.GONE);
        cancel_tv.setVisibility(View.GONE);
        decline_img.setVisibility(View.GONE);
        decline_tv.setVisibility(View.GONE);
        imageAttend.setVisibility(View.GONE);
        accept_tv.setVisibility(View.GONE);

    }

    public void startStopChronometer() {
        if (isStart) {
            chronometer.stop();
            isStart = false;

        } else {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            isStart = true;

        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Tutorial Step 1
        joinChannel();               // Tutorial Step 2
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        leaveChannel();
        if (player.isPlaying()) {
            player.stop();

        }
        RtcEngine.destroy();
//        mRtcEngine = null;
        super.onDestroy();
    }

    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());

    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        onBackPressed();
    }

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel() {
//        String accessToken = getApplicationContext().getResources().getString(R.string.agora_access_token_voice);
//        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, getString(R.string.agora_access_token_voice))) {
//            accessToken = null; // default, no token
//        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

//        Log.d("ChannelsDetails", "name" + channelName + "\nToken:" + channeltoken);

//        channeltoken = "0063e9f393f860e4cbbb2a1b76b96505ac7IACt7RmG0kvXLsbA0spexpy7ETWn8kGFukaSRatG+caiurdIfRAAAAAAIgDPvwAAx4MBYAQAAQBXQABgAwBXQABgAgBXQABgBABXQABg";
//        channelName = "7d72365eb983485397e3e3f9d460bdda";
        // Allows a user to join a channel.
        mRtcEngine.joinChannel(channeltoken, channelName, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    public void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    @Override
    public void onSuccess(Void aVoid) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onFailure(ErrorInfo errorInfo) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onMemberCountUpdated(int i) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onAttributesUpdated(List<RtmChannelAttribute> list) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onMemberJoined(RtmChannelMember rtmChannelMember) {
        Log.d("TAG", "onSuccess: ");
    }

    @Override
    public void onMemberLeft(RtmChannelMember rtmChannelMember) {
        Log.d("TAG", "onSuccess: ");
    }
}
