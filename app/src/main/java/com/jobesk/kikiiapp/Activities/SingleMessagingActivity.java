package com.jobesk.kikiiapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.jobesk.kikiiapp.Adapters.Chat.MessagingAdapter;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Firebase.ChangeEventListener;
import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.jobesk.kikiiapp.Firebase.Model.Message;
import com.jobesk.kikiiapp.Firebase.Services.LastMessageService;
import com.jobesk.kikiiapp.Firebase.Services.SeenMessagingService;
import com.jobesk.kikiiapp.Firebase.Services.SingleMessagingService;
import com.jobesk.kikiiapp.Firebase.Services.UserService;
import com.jobesk.kikiiapp.Model.ChannelNameModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;

import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.calling.VideoChatViewActivity;
import com.jobesk.kikiiapp.calling.VoiceChatViewActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleMessagingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = "MessagingActivity";
    private Context context = SingleMessagingActivity.this;
    private Activity activity = SingleMessagingActivity.this;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_messaging;
    private MessagingAdapter messagingAdapter;
    private LinearLayoutManager layoutManager;
    private List<Message> messagesList = new ArrayList<>();

    private String receiverId, name, receiverPic;
    private FirebaseUserModel user;
    private EditText et_comment;

    Message message = new Message();

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA_q7nAiQ:APA91bFqSjnVDK9pa4WwOj-YofbMyQaCKjmVvhwYVPeczgACsu9k_VxDmEKojtKaz-t7UOgU1WWaTbOnQ_zC3mhy66Kzspq9ThCzgiCYqioviU_gHFRIGBJgoWyh4xtwaMqzIFPr8cJe";
    final private String contentType = "application/json";

    private String NOTIFICATION_TITLE;
    private String NOTIFICATION_MESSAGE;
    private String USER_ID;
    private String TOPIC = "/topics/chat";

    private SingleMessagingService singleMessagingService;
    private SeenMessagingService seenMessagingService;
    private LastMessageService lastMessageService;
    private ImageView img_send, img_record_audio, img_cancel_audio, img_camera;
    private TextView tv_name;
    private Chronometer chronometer_recording;
    private int pauseOffset = 0;
    private LinearLayout ll_recording_audio;
    private boolean isRecordedAudio = false;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private CustomLoader customLoader;
    private AlertDialog alertDialog;
    private long recordingElapsedMillis;
    private int count = 0;
    private UserService userService;
    private boolean isDeletingMessages = false;
    private List<String> deletedMessagesList = new ArrayList<>();

    /****/
    private View header_normal, header_delete_messages;
    private TextView tv_count;
    private ImageView img_delete;

    /****/
    private ImageView img_video_call, img_voice_call;

    String customerID;
    // 1 for voice call
    // 2 for video call

    private int callStatus = 1;


    private RelativeLayout mRemoteVideo;
    DatabaseReference firebaseDatabaseChatsReceiver;
    ValueEventListener messageSeenListnerReceiver;

    DatabaseReference firebaseDatabaseChatsSender;
    ValueEventListener messageSeenListnerSender;
    String receiver_realID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_messaging);

        getIntentData();
        initComponents();

        //tv_name.setText(name);

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

        lastMessageService = new LastMessageService(AppState.currentBpackCustomer.getUserId(), receiverId);
        lastMessageService.setOnChangedListener(new ChangeEventListener() {
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


//        seenMessagingService = new SeenMessagingService(receiverId, AppState.currentBpackCustomer.getUserId());
//        seenMessagingService.setOnChangedListener(new ChangeEventListener() {
//            @Override
//            public void onChildChanged(EventType type, int index, int oldIndex) {
//
//            }
//
//            @Override
//            public void onDataChanged() {
//                if (seenMessagingService.getCount() > 0) {
//                    for (int i = 0; i < seenMessagingService.getCount(); i++) {
//                        DataSnapshot sp = seenMessagingService.getItem(i);
//                        Message message = sp.getValue(Message.class);
//                        if (message.getSeen().equalsIgnoreCase("false")) {
//                            FirebaseUserModel receiver = userService.getUserById(receiverId);
////                            if (receiver.getIsOnline().equalsIgnoreCase("true")) {
//                            seenMessagingService.updateSeenStatus(sp.getKey());
////                            }
//
//                        }
//                    }
//                    messagingAdapter.addAll(messagesList);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });


        singleMessagingService = new SingleMessagingService(AppState.currentBpackCustomer.getUserId(), receiverId);
        singleMessagingService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {
            }

            @Override
            public void onDataChanged() {
                messagesList.clear();
                if (singleMessagingService.getCount() > 0) {
                    for (int i = 0; i < singleMessagingService.getCount(); i++) {
                        DataSnapshot sp = singleMessagingService.getItem(i);
                        Message message = sp.getValue(Message.class);

                        String id = AppState.currentBpackCustomer.getUserId();
                        if (message.getUserId() != null) {
                            if (!message.getUserId().equalsIgnoreCase(id)) {
                                messagesList.add(message);
                            }
                        }

                    }
                    messagingAdapter.addAll(messagesList);
                    rv_messaging.scrollToPosition(messagesList.size() - 1);
                }
                if (messagesList.size() == 0) {
                    tv_no.setVisibility(View.VISIBLE);
                    rv_messaging.setVisibility(View.GONE);
                } else {
                    tv_no.setVisibility(View.GONE);
                    rv_messaging.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);

        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_record_audio.setVisibility(View.GONE);
                    img_send.setVisibility(View.VISIBLE);
                } else {
                    img_record_audio.setVisibility(View.VISIBLE);
                    img_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    protected void onDestroy() {


        if (firebaseDatabaseChatsReceiver != null && messageSeenListnerReceiver != null) {
            firebaseDatabaseChatsReceiver.removeEventListener(messageSeenListnerReceiver);
        }

        if (firebaseDatabaseChatsSender != null && messageSeenListnerSender != null) {
            firebaseDatabaseChatsSender.removeEventListener(messageSeenListnerSender);
        }


        super.onDestroy();

    }

    private void setSeenReceiver() {
        String current_UserID = AppState.currentBpackCustomer.getUserId();

        Log.d("getIdz", "setSeenReceiver: " + receiverId + "    senderId:" + AppState.currentBpackCustomer.getUserId());

        String end = receiverId + "___" + current_UserID;

        firebaseDatabaseChatsReceiver = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/").getReference("Chats").child(end);
        messageSeenListnerReceiver = firebaseDatabaseChatsReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d("count", "onDataChange: " + count);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message message = child.getValue(Message.class);
                    String messageID = message.getMessageId();
                    firebaseDatabaseChatsReceiver.child(messageID).child("seen").setValue("true");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setSeenSender() {
        String current_UserID = AppState.currentBpackCustomer.getUserId();

        Log.d("getIdz", "setSeenReceiver: " + receiverId + "    senderId:" + AppState.currentBpackCustomer.getUserId());

        String end = current_UserID + "___" + receiverId;

        firebaseDatabaseChatsSender = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com/").getReference("Chats").child(end);
        messageSeenListnerSender = firebaseDatabaseChatsSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d("count", "onDataChange: " + count);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message message = child.getValue(Message.class);
                    String messageID = message.getMessageId();
                    firebaseDatabaseChatsSender.child(messageID).child("seen").setValue("true");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCallToken(String type) {
//

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());

//        types are
//                audio //video

        Call<ChannelNameModel> call = api.makeCall(sessionManager.getAccessToken(), type, String.valueOf(customerID));
        call.enqueue(new Callback<ChannelNameModel>() {
            @Override
            public void onResponse(Call<ChannelNameModel> call, Response<ChannelNameModel> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();
                if (response.isSuccessful()) {

                    ChannelNameModel channelNameModel = response.body();
                    String getChannelName = channelNameModel.getChannelName();
                    String userIDCurrent = sessionManager.getUserID();
                    String getChannelToken = channelNameModel.getToken();
                    if (type.equalsIgnoreCase("audio")) {

                        Intent intent = new Intent(SingleMessagingActivity.this, VoiceChatViewActivity.class);
                        intent.putExtra("channelName", getChannelName);
                        intent.putExtra("channeltoken", getChannelToken);
                        intent.putExtra("receiverName", name);
                        intent.putExtra("receiverImage", receiverPic);
                        intent.putExtra("senderName", sessionManager.getUserName());
                        intent.putExtra("senderImage", sessionManager.getPhoto());
                        intent.putExtra("receiverID", customerID);
                        intent.putExtra("sender_id", userIDCurrent);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(SingleMessagingActivity.this, VideoChatViewActivity.class);

                        intent.putExtra("channelName", getChannelName);
                        intent.putExtra("channeltoken", getChannelToken);
                        intent.putExtra("receiverName", name);
                        intent.putExtra("receiverImage", receiverPic);
                        intent.putExtra("senderName", sessionManager.getUserName());
                        intent.putExtra("senderImage", sessionManager.getPhoto());
                        intent.putExtra("receiverID", customerID);
                        intent.putExtra("sender_id", userIDCurrent);
                        startActivity(intent);
                    }
                }
//                ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
            }

            @Override
            public void onFailure(Call<ChannelNameModel> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void getIntentData() {

        receiverId = getIntent().getStringExtra(Constants.ID);
        name = getIntent().getStringExtra(Constants.START_NAME);
        receiverPic = getIntent().getStringExtra(Constants.IMAGE);

        user = (FirebaseUserModel) getIntent().getSerializableExtra(Constants.CREATE_RIDE_OBJ);


        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://kikiiapp-default-rtdb.firebaseio.com");
        DatabaseReference ref = database.getReference("Users/" + receiverId);

        ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                customerID = dataSnapshot.child("id").getValue().toString();
                receiver_realID = dataSnapshot.child("userId").getValue().toString();


                Log.d(TAG, "onDataChange: " + customerID);
//                setSeenReceiver();
//
//                SessionManager sessionManager = new SessionManager(SingleMessagingActivity.this);
//                String id = sessionManager.getUserID();

                if (receiverId.equalsIgnoreCase(customerID)) {
                    // its current user
                    setSeenSender();
                } else {
                    // its other user
                    setSeenReceiver();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }

    private void initComponents() {


//        Firebase.setAndroidContext(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        customLoader = new CustomLoader(activity, false);

        header_normal = findViewById(R.id.header_normal);
        header_delete_messages = findViewById(R.id.header_delete_messages);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        rv_messaging = findViewById(R.id.rv_messaging);

        layoutManager = new LinearLayoutManager(context);
        messagingAdapter = new MessagingAdapter(messagesList, context, activity, user);
        rv_messaging.setLayoutManager(layoutManager);
        rv_messaging.setAdapter(messagingAdapter);

        img_send = findViewById(R.id.img_send);
        img_cancel_audio = findViewById(R.id.img_cancel_audio);
        img_record_audio = findViewById(R.id.img_record_audio);
        img_camera = findViewById(R.id.img_camera);
        img_delete = findViewById(R.id.img_delete);

        tv_no = findViewById(R.id.tv_no);
        tv_count = findViewById(R.id.tv_count);

        chronometer_recording = findViewById(R.id.chronometer_recording);

        et_comment = findViewById(R.id.et_comment);

        ll_recording_audio = findViewById(R.id.ll_recording_audio);
        mRemoteVideo = findViewById(R.id.remote_video_view_container);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audio_" + UUID.randomUUID().toString() + ".3gp";


        img_video_call = findViewById(R.id.img_video_call);
        img_voice_call = findViewById(R.id.img_voice_call);
        SessionManager sessionManager = new SessionManager(SingleMessagingActivity.this);
        boolean isPaid = sessionManager.getIsPaid();
        if (isPaid) {
            img_video_call.setVisibility(View.VISIBLE);
            img_voice_call.setVisibility(View.VISIBLE);
        } else {
            img_video_call.setVisibility(View.GONE);
            img_voice_call.setVisibility(View.GONE);
        }


        messagingAdapter.setOnLongPressListener(new MessagingAdapter.IClick() {
            @Override
            public void onLongPressListener(View v, Message message) {
                if (!isDeletingMessages) {
                    header_delete_messages.setVisibility(View.VISIBLE);
                    header_normal.setVisibility(View.GONE);
                    isDeletingMessages = true;
                    deletedMessagesList.add(message.getMessageId());
                    v.setBackgroundColor(context.getResources().getColor(R.color.dim_pink));
                    tv_count.setText(String.valueOf(deletedMessagesList.size()));
                }
            }

            @Override
            public void onClickListener(View v, Message message) {
                if (isDeletingMessages) {
                    if (!deletedMessagesList.contains(message.getMessageId())) {
                        deletedMessagesList.add(message.getMessageId());
                        v.setBackgroundColor(context.getResources().getColor(R.color.dim_pink));
                        tv_count.setText(String.valueOf(deletedMessagesList.size()));
                    } else {
                        deletedMessagesList.remove(message.getMessageId());
                        v.setBackgroundResource(R.color.transparent);
                        tv_count.setText(String.valueOf(deletedMessagesList.size()));
                        if (deletedMessagesList.size() == 0) {
                            header_normal.setVisibility(View.VISIBLE);
                            header_delete_messages.setVisibility(View.GONE);
                            isDeletingMessages = false;

                        }
                    }
                }
            }
        });

        img_voice_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callStatus = 1;
                checkPermissionsCalls();


            }
        });
        img_video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStatus = 2;
                checkPermissionsCalls();
            }
        });

        ImageView img_menu = findViewById(R.id.img_menu);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
    }

    private void showPopup(View view) {


        Context wrapper = new ContextThemeWrapper(SingleMessagingActivity.this, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
//        PopupMenu popup = new PopupMenu(SingleMessagingActivity.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu_message, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.view_profile) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra(Constants.ID, String.valueOf(customerID));
                    activity.startActivity(intent);
                }
                return true;
            }
        });
        popup.show();
    }

    private void checkPermissionsCalls() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()) {


                    if (callStatus == 1) {
                        // go to voice call

                        getCallToken("audio");
                    }
                    if (callStatus == 2) {
                        // go to video call
                        getCallToken("video");

                    }


                }


            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();


            }
        }).check();


    }

    @Override
    public void onRefresh() {
        singleMessagingService = new SingleMessagingService(AppState.currentBpackCustomer.getUserId(), receiverId);
        singleMessagingService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
                messagesList.clear();
                if (singleMessagingService.getCount() > 0) {
                    for (int i = 0; i < singleMessagingService.getCount(); i++) {
                        DataSnapshot sp = singleMessagingService.getItem(i);
                        Message message = sp.getValue(Message.class);
                        if (!message.getUserId().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                            messagesList.add(message);
                        }
                    }
                    messagingAdapter.addAll(messagesList);
                    rv_messaging.scrollToPosition(messagesList.size() - 1);
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_send:
                if (isRecordedAudio) {
                    stopRecordingAndSend();
                } else {
                    if (et_comment.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Enter Text!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        sendMessage(Constants.TYPE_TEXT, et_comment.getText().toString());
                    }
                }
                break;
            case R.id.img_cancel_audio:
                stopRecording();
                break;
            case R.id.img_record_audio:
                requestAudioPermissions();
                break;
            case R.id.img_camera:
                Dexter.withContext(activity)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            MediaSelectionAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
                break;
            case R.id.img_delete:
                if (isDeletingMessages) {
                    final Dialog alertDialog = new Dialog(context);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.dialogue_delete_messages);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.findViewById(R.id.tv_btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.findViewById(R.id.tv_btn_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (singleMessagingService != null) {
                                alertDialog.dismiss();
                                singleMessagingService.deleteMessages(deletedMessagesList, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        recreate();
                                    }
                                });
                            }
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }

    private void stopRecordingAndSend() {
        recorder.stop();
        recorder.release();
        recorder = null;
        resetTextingView();
        recordingElapsedMillis = SystemClock.elapsedRealtime() - chronometer_recording.getBase();
        UploadAudioToStorage();
        chronometer_recording.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        chronometer_recording.stop();
    }

    private void sendMessage(final String type, final String message) {
        //String message = et_comment.getText().toString();
        if (singleMessagingService == null) {
            singleMessagingService = new SingleMessagingService(AppState.currentBpackCustomer.getUserId(), receiverId);
            singleMessagingService.setOnChangedListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(EventType type, int index, int oldIndex) {

                }

                @Override
                public void onDataChanged() {
               /* messagesList.clear();
                if (chatService.getCount() > 0) {
                    for (int i = 0; i < chatService.getCount(); i++) {
                        DataSnapshot sp = chatService.getItem(i);
                        Chat chat = sp.getValue(Chat.class);
                        if (chat.getUserId().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                            messagesList.add(chat);
                        }
                    }
                    if (messagingAdapter != null) {
                        messagingAdapter.addAll(messagesList);
                    } else {

                    }
                }*/
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
            singleMessagingService.sendMessage(message, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    et_comment.setText("");
                    lastMessageService.updateLastMessage(message, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            singleMessagingService.sendMessage(message, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    et_comment.setText("");
                    lastMessageService.updateLastMessage(message, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendMessage(final String type, final String message, final long recordingElapsedMillis) {
        //String message = et_comment.getText().toString();
        if (singleMessagingService == null) {
            singleMessagingService = new SingleMessagingService(AppState.currentBpackCustomer.getUserId(), receiverId);
            singleMessagingService.setOnChangedListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(EventType type, int index, int oldIndex) {

                }

                @Override
                public void onDataChanged() {
               /* messagesList.clear();
                if (chatService.getCount() > 0) {
                    for (int i = 0; i < chatService.getCount(); i++) {
                        DataSnapshot sp = chatService.getItem(i);
                        Chat chat = sp.getValue(Chat.class);
                        if (chat.getUserId().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                            messagesList.add(chat);
                        }
                    }
                    if (messagingAdapter != null) {
                        messagingAdapter.addAll(messagesList);
                    } else {

                    }
                }*/
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
            singleMessagingService.sendMessage(message, recordingElapsedMillis, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    et_comment.setText("");
                    lastMessageService.updateLastMessage(message, recordingElapsedMillis, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            singleMessagingService.sendMessage(message, recordingElapsedMillis, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    et_comment.setText("");
                    lastMessageService.updateLastMessage(message, recordingElapsedMillis, AppState.currentBpackCustomer.getUserId(), receiverId, type, AppState.currentBpackCustomer.getUserId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            et_comment.setText("");

                            //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(context, "sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /***RECORDING AUDIO CODE***/
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                //Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        permissions,
                        REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            setRecordingAudioView();
            startRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        chronometer_recording.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer_recording.start();
        recorder.start();
    }


    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        resetTextingView();
        chronometer_recording.stop();
        chronometer_recording.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private void resetTextingView() {
        isRecordedAudio = false;
        ll_recording_audio.setVisibility(View.GONE);
        img_record_audio.setVisibility(View.VISIBLE);
        et_comment.setText("");
        et_comment.setEnabled(true);
        img_camera.setClickable(true);
    }

    private void setRecordingAudioView() {
        isRecordedAudio = true;
        ll_recording_audio.setVisibility(View.VISIBLE);
        img_record_audio.setVisibility(View.GONE);

        et_comment.setText("Recording Audio...");
        et_comment.setEnabled(false);
        img_camera.setClickable(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) resetTextingView();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void UploadAudioToStorage() {
        customLoader.showIndicator();
        final StorageReference ref = storageReference.child("gs://kikii-1cdea.appspot.com/chat_audios/audio_" + UUID.randomUUID().toString());
        Uri uri = Uri.fromFile(new File(fileName));
        ref.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String planImgLink = uri.toString();
                                sendMessage(Constants.TYPE_AUDIO, planImgLink, recordingElapsedMillis);
                                customLoader.hideIndicator();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Error in getting image link", Toast.LENGTH_SHORT).show();
                                customLoader.hideIndicator();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customLoader.hideIndicator();
            }
        });
    }

    private void MediaSelectionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Media:");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.media_selection_dialog, null);
        builder.setView(customLayout);

        final ImageView selection_img = customLayout.findViewById(R.id.selection_img);
        final ImageView selection_video = customLayout.findViewById(R.id.selection_video);
        TextView image_tv = customLayout.findViewById(R.id.image_tv);
        TextView video_tv = customLayout.findViewById(R.id.video_tv);


        selection_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImages();
                alertDialog.dismiss();

            }
        });
        image_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection_img.callOnClick();
            }
        });

        selection_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickVideo();
                alertDialog.dismiss();
            }
        });
        video_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection_video.callOnClick();
            }
        });
        alertDialog = builder.show();
        alertDialog.show();
    }

    private void PickImages() {
        new ImagePicker.Builder(activity)
                .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.NONE)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.JPG)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }

    private void PickVideo() {
        new VideoPicker.Builder(activity)
                .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                .directory(VideoPicker.Directory.DEFAULT)
                .extension(VideoPicker.Extension.MP4)
                .enableDebuggingMode(true)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            String path = mPaths.get(0);
            customLoader.showIndicator();
            final StorageReference ref = storageReference.child("gs://cooplas-be80d.appspot.com/chat_images/image_" + UUID.randomUUID().toString());
            Uri uri = Uri.fromFile(new File(path));
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String planImgLink = uri.toString();
                                    sendMessage(Constants.TYPE_IMAGE, planImgLink);
                                    customLoader.hideIndicator();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error in getting image link", Toast.LENGTH_SHORT).show();
                                    customLoader.hideIndicator();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    customLoader.hideIndicator();
                }
            });
        }
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

            List<String> mPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            String path = mPaths.get(0);
            customLoader.showIndicator();
            final StorageReference ref = storageReference.child("gs://kikii-1cdea.appspot.com/chat_videos/video_" + UUID.randomUUID().toString());
            Uri uri = Uri.fromFile(new File(path));
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String planImgLink = uri.toString();
                                    sendMessage(Constants.TYPE_VIDEO, planImgLink);
                                    customLoader.hideIndicator();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error in getting image link", Toast.LENGTH_SHORT).show();
                                    customLoader.hideIndicator();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    customLoader.hideIndicator();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (isDeletingMessages) {
            recreate();
        } else {
            super.onBackPressed();
        }
    }
}