package com.jobesk.kikiiapp.Utils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jobesk.kikiiapp.Activities.MatchDoneActivity;
import com.jobesk.kikiiapp.Activities.PostDetailActivity;
import com.jobesk.kikiiapp.Activities.SplashActivity;
import com.jobesk.kikiiapp.calling.VideoChatViewActivity;
import com.jobesk.kikiiapp.calling.VoiceChatViewActivity;
import com.jobesk.kikiiapp.R;

import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "MyFirebaseMessagingService";
    Intent intent;
    String channelId;
    String channelName;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // android want notificaiton in data

            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String type = remoteMessage.getData().get("type");

            if (type.equalsIgnoreCase("audio")) {

                channelId = "callings";
                channelName = "Audio Video Call";

                String senderID = remoteMessage.getData().get("sender_id");
                String receiver_id = remoteMessage.getData().get("receiver_id");

                intent = new Intent(this, VoiceChatViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("channelName", remoteMessage.getData().get("channel_name"));
                intent.putExtra("channeltoken", remoteMessage.getData().get("token"));
                intent.putExtra("receiverName", remoteMessage.getData().get("receiver_name"));
                intent.putExtra("receiverImage", remoteMessage.getData().get("receiver_image"));
                intent.putExtra("senderName", remoteMessage.getData().get("sender_name"));
                intent.putExtra("senderImage", remoteMessage.getData().get("sender_image"));
                intent.putExtra("receiverID", receiver_id);
                intent.putExtra("sender_id", senderID);

                showNotification(remoteMessage);

            }

            if (type.equalsIgnoreCase("video")) {

                channelId = "callings";
                channelName = "Audio Video Call";

                String senderID = remoteMessage.getData().get("sender_id");
                String receiver_id = remoteMessage.getData().get("receiver_id");

                intent = new Intent(this, VideoChatViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("channelName", remoteMessage.getData().get("channel_name"));
                intent.putExtra("channeltoken", remoteMessage.getData().get("token"));
                intent.putExtra("receiverName", remoteMessage.getData().get("receiver_name"));
                intent.putExtra("receiverImage", remoteMessage.getData().get("receiver_image"));
                intent.putExtra("senderName", remoteMessage.getData().get("sender_name"));
                intent.putExtra("senderImage", remoteMessage.getData().get("sender_image"));
                intent.putExtra("receiverID", receiver_id);
                intent.putExtra("sender_id", senderID);

                showNotification(remoteMessage);
            }
            if (type.equalsIgnoreCase("missed_call")) {
                Intent intent = new Intent("close_Call");

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

//                boolean value = isActivityRunning(VoiceChatViewActivity.class);
//                if (value) {
//                    Intent intent = new Intent("close_Call");
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
////                    VoiceChatViewActivity
//                }
            }
            if (type.equalsIgnoreCase("post_like")) {
                channelId = "Post Like";
                channelName = "Post Like Notification";
                Log.d(TAG, "onMessageReceived: ");
                String postID = remoteMessage.getData().get("post_id");
//                String ID = remoteMessage.getData().get("id");
//                String BOYD = remoteMessage.getData().get("body");
                if (isAppOnForeground(getApplicationContext())) {
                    intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("postID", postID);
                } else {
                    intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.putExtra("postID", postID);
                    intent.putExtra("type", remoteMessage.getData().get("type"));
                }
                showNotification(remoteMessage);
            }
            if (type.equalsIgnoreCase("post_comment")) {
                channelId = "Post Comment";
                channelName = "Post Comment Notification";
                Log.d(TAG, "onMessageReceived: ");
                String postID = remoteMessage.getData().get("post_id");
//                String ID = remoteMessage.getData().get("id");
//                String BOYD = remoteMessage.getData().get("body");
                if (isAppOnForeground(getApplicationContext())) {
                    intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("postID", postID);
                } else {
                    intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.putExtra("postID", postID);
                    intent.putExtra("type", remoteMessage.getData().get("type"));
                }
                showNotification(remoteMessage);
            }
            if (type.equalsIgnoreCase("comment_reply")) {
                channelId = "Post Comment Reply";
                channelName = "Post Comment Reply Notification";
                Log.d(TAG, "onMessageReceived: ");
                String postID = remoteMessage.getData().get("post_id");
//                String ID = remoteMessage.getData().get("id");
//                String BOYD = remoteMessage.getData().get("body");
                if (isAppOnForeground(getApplicationContext())) {
                    intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("postID", postID);
                } else {
                    intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.putExtra("postID", postID);
                    intent.putExtra("type", remoteMessage.getData().get("type"));
                }
                showNotification(remoteMessage);
            }

            if (type.equalsIgnoreCase("match")) {
//                Log.d("appstatus", "onMessageReceived: " + isAppRunning());
                channelId = "Match";
                channelName = "User Matching";
                if (isAppOnForeground(getApplicationContext())) {
                    intent = new Intent(this, MatchDoneActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("otherUserImage", remoteMessage.getData().get("profile_pic"));
                    intent.putExtra("otherUserID", remoteMessage.getData().get("id"));
                    startActivity(intent);
                } else {
                    intent = new Intent(this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("otherUserImage", remoteMessage.getData().get("profile_pic"));
                    intent.putExtra("otherUserID", remoteMessage.getData().get("id"));
                    intent.putExtra("type", remoteMessage.getData().get("type"));
                    showNotification(remoteMessage);
                }
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage);
        }
    }

    protected Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    public static boolean isAppOnForeground(Context context) {
        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    //    private boolean isAppRunning() {
//        ActivityManager m = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = m.getRunningTasks(10);
//        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
//        int n = 0;
//        while (itr.hasNext()) {
//            n++;
//            itr.next();
//        }
//        if (n == 1) { // App is killed
//            return false;
//        }
//
//        return true; // App is in background or foreground
//    }
    public void showNotification(RemoteMessage remoteMessage) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
