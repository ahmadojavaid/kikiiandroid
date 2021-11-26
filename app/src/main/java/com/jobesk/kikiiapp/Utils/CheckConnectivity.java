package com.jobesk.kikiiapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.jobesk.kikiiapp.Events.CheckInternetEvent;

import org.greenrobot.eventbus.EventBus;

public class CheckConnectivity extends BroadcastReceiver {
    private final EventBus eventBus = EventBus.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean status = isNetworkAvailable(context);
        Log.d("receiverr", "onReceive: "+status);
        if (isInitialStickyBroadcast()) {
            Log.d("sss", "onReceive: ignore");
        } else {
            EventBus.getDefault().postSticky(new CheckInternetEvent(status));
        }
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

