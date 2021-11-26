package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobesk.kikiiapp.Adapters.ChattingAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetConversationMessages;
import com.jobesk.kikiiapp.Callbacks.CallbackSendMessage;
import com.jobesk.kikiiapp.Model.Message;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ChattingActivity";
    private Context context = ChattingActivity.this;
    private Activity activity = ChattingActivity.this;

    private RecyclerView rv_chatting;
    private ChattingAdapter chattingAdapter;
    private List<Message> chatList = new ArrayList<>();

    private ImageView img_video_call, img_voice_call;
    private EditText et_message;
    private ImageView img_voice, img_send;

    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private LinearLayoutManager linearLayoutManager;

    private Call<CallbackGetConversationMessages> callbackGetConversationMessagesCall;
    private CallbackGetConversationMessages responseGetConversationMessages;

    private Call<CallbackSendMessage> callbackSendMessageCall;
    private CallbackSendMessage responseSendMessage;

    private Map<String, String> sendMessageParam = new HashMap<>();

    private String conversationId, userMatchId;
    private int count = 0;
    private TextView tv_no;
    private SwipeRefreshLayout swipe_refresh_layout;
    private boolean isSwipeRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        getIntentData();
        initComponents();

        if (conversationId != null) {
            loadChat1(conversationId);
            sendMessageParam.put(Constants.CONVERSATION_ID, conversationId);
        } else if (userMatchId != null) {
            loadChat2(userMatchId);
            sendMessageParam.put(Constants.USER_MATCH_ID, userMatchId);
        }
        else
        tv_no.setVisibility(View.VISIBLE);


        img_video_call.setOnClickListener(this);
        img_voice_call.setOnClickListener(this);
        img_send.setOnClickListener(this);
        swipe_refresh_layout.setOnRefreshListener(this);


        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    img_voice.setVisibility(View.GONE);
                    img_send.setVisibility(View.VISIBLE);
                } else {
                    img_voice.setVisibility(View.VISIBLE);
                    img_send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getIntentData() {
        userMatchId = getIntent().getStringExtra(Constants.USER_MATCH_ID);
        conversationId = getIntent().getStringExtra(Constants.CONVERSATION_ID);
    }

    private void loadChat1(String id) {
        chatList.clear();
        if (!isSwipeRefresh)
            customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetConversationMessagesCall = api.getConversationMessages1(sessionManager.getAccessToken(), id);
        callbackGetConversationMessagesCall.enqueue(new Callback<CallbackGetConversationMessages>() {
            @Override
            public void onResponse(Call<CallbackGetConversationMessages> call, Response<CallbackGetConversationMessages> response) {
                Log.d(TAG, "onResponse: " + response);
                responseGetConversationMessages = response.body();
                if (responseGetConversationMessages != null) {
                    if (responseGetConversationMessages.getSuccess()) {
                        swipe_refresh_layout.setRefreshing(false);
                        isSwipeRefresh = false;
                        if (responseGetConversationMessages.getMessages().size() > 0) {
                            rv_chatting.setVisibility(View.VISIBLE);
                            tv_no.setVisibility(View.GONE);
                            setData();
                        } else {
                            customLoader.hideIndicator();
                            tv_no.setVisibility(View.VISIBLE);
                            rv_chatting.setVisibility(View.GONE);
                        }
                        /*else
                            currentPage = -1;*/
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetConversationMessages.getMessage());
                        Toast.makeText(context, responseGetConversationMessages.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetConversationMessages> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void loadChat2(String id) {
        chatList.clear();
        if (!isSwipeRefresh)
            customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetConversationMessagesCall = api.getConversationMessages2(sessionManager.getAccessToken(), id);
        callbackGetConversationMessagesCall.enqueue(new Callback<CallbackGetConversationMessages>() {
            @Override
            public void onResponse(Call<CallbackGetConversationMessages> call, Response<CallbackGetConversationMessages> response) {
                Log.d(TAG, "onResponse: " + response);
                responseGetConversationMessages = response.body();
                if (responseGetConversationMessages != null) {
                    if (responseGetConversationMessages.getSuccess()) {
                        swipe_refresh_layout.setRefreshing(false);
                        isSwipeRefresh = false;
                        if (responseGetConversationMessages.getMessages().size() > 0) {
                            rv_chatting.setVisibility(View.VISIBLE);
                            tv_no.setVisibility(View.GONE);
                            setData();
                        } else {
                            customLoader.hideIndicator();
                            tv_no.setVisibility(View.VISIBLE);
                            rv_chatting.setVisibility(View.GONE);
                        }
                        /*else
                            currentPage = -1;*/
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetConversationMessages.getMessage());
                        Toast.makeText(context, responseGetConversationMessages.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetConversationMessages> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {
        conversationId = responseGetConversationMessages.getMessages().get(0).getConversationId().toString();
        sendMessageParam.put(Constants.CONVERSATION_ID, conversationId);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_chatting.setLayoutManager(linearLayoutManager);
        //currentPage = responseGetConversationMessages.getNextOffset();
        chatList = responseGetConversationMessages.getMessages();
        chattingAdapter.addAll(chatList);
        rv_chatting.setAdapter(chattingAdapter);
        customLoader.hideIndicator();
    }

    private void initComponents() {
        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);

        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        img_video_call = findViewById(R.id.img_video_call);
        img_voice_call = findViewById(R.id.img_voice_call);
        img_send = findViewById(R.id.img_send);
        img_voice = findViewById(R.id.img_voice);

        et_message = findViewById(R.id.et_message);

        rv_chatting = findViewById(R.id.rv_chatting);
        chattingAdapter = new ChattingAdapter(activity);
        chattingAdapter.addAll(chatList);

        tv_no = findViewById(R.id.tv_no);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_video_call:
                startActivity(new Intent(context, CallingActivity.class));
                break;
            case R.id.img_voice_call:
                startActivity(new Intent(context, CallingActivity.class));
                break;
            case R.id.img_send:
                if (et_message.getText().toString().isEmpty()) {
                    et_message.setError(getResources().getString(R.string.et_error));
                } else {
                    sendMessageParam.put(Constants.BODY, et_message.getText().toString());
                    sendMessageParam.put(Constants.RECEIVER_ID, userMatchId);
                    sendMessage();
                }
                break;
        }
    }

    private void sendMessage() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackSendMessageCall = api.sendMessage(sessionManager.getAccessToken(), sendMessageParam);
        callbackSendMessageCall.enqueue(new Callback<CallbackSendMessage>() {
            @Override
            public void onResponse(Call<CallbackSendMessage> call, Response<CallbackSendMessage> response) {
                Log.d(TAG, "onResponse: " + response);
                responseSendMessage = response.body();
                if (responseSendMessage != null) {
                    if (responseSendMessage.getSuccess()) {
                        customLoader.hideIndicator();
                        Message message = responseSendMessage.getData();
                        sendMessageParam.clear();
                        conversationId = message.getConversationId().toString();
                        sendMessageParam.put(Constants.CONVERSATION_ID, conversationId);
                        chattingAdapter.add(message);
                        et_message.setText("");
                        tv_no.setVisibility(View.GONE);
                        View view = ChattingActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetConversationMessages.getMessage());
                        Toast.makeText(context, responseGetConversationMessages.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackSendMessage> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        chatList.clear();
        isSwipeRefresh = true;
        if (conversationId != null)
            loadChat1(conversationId);
        else if (userMatchId != null)
            loadChat2(userMatchId);
        else
            tv_no.setVisibility(View.VISIBLE);
    }
}
