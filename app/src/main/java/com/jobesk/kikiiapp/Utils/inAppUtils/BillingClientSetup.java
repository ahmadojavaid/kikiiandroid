package com.jobesk.kikiiapp.Utils.inAppUtils;

import android.content.Context;

import com.airbnb.lottie.animation.content.Content;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

/**
 * Created by Sheraz Ahmed on 1/12/2021.
 * sherazbhutta07@gmail.com
 */
public class BillingClientSetup {


    private static BillingClient instance;


    public static BillingClient getInstance(Context context, PurchasesUpdatedListener listner) {


        return instance == null ? setupBillingClient(context, listner) : instance;


    }

    private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listner) {

        BillingClient billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listner)
                .build();

        return billingClient;
    }

}
