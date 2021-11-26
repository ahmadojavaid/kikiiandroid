package com.jobesk.kikiiapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Sheraz Ahmed on 1/27/2021.
 * sherazbhutta07@gmail.com
 */
public class BroadcastReceiverMatch extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context, "triggered", Toast.LENGTH_SHORT).show();


    }
}
