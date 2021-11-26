package com.jobesk.kikiiapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.jobesk.kikiiapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/12/2021.
 * sherazbhutta07@gmail.com
 */
public class Billing extends AppCompatActivity implements PurchasesUpdatedListener {
    Button buy_btn;
    TextView tv;
    BillingClient billingClient;
    List<String> skulist = new ArrayList<>();
    String product = "one_month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        buy_btn = findViewById(R.id.buy_button_1);
        tv = findViewById(R.id.tv);

        billingClient = BillingClient.newBuilder(Billing.this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            //This method starts when user buys a product
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        handlepurchase(purchase);
                    }
                } else {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Toast.makeText(Billing.this, "Try Purchasing Again", Toast.LENGTH_SHORT).show();
                    } else {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            Toast.makeText(Billing.this, "Already Purchased", Toast.LENGTH_SHORT).show();
                            //We recover that method by consuming a purchase so that user can buy a product again from same account.
                        }
                    }
                }
            }
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(Billing.this, "Successfully connected to Billing client", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Billing.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(Billing.this, "Disconnected from the Client", Toast.LENGTH_SHORT).show();
            }
        });
        skulist.add(product);
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skulist).setType(BillingClient.SkuType.SUBS);  //Skutype.subs for Subscription
        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
                        if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (final SkuDetails skuDetails : list) {
                                String sku = skuDetails.getSku(); // your Product id
                                String price = skuDetails.getPrice(); // your product price
                                String description = skuDetails.getDescription(); // product description
                                //method opens Popup for billing purchase
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build();
                                BillingResult responsecode = billingClient.launchBillingFlow(Billing.this, flowParams);
                            }
                        }
                    }
                });
            }
        });
    }

    private void handlepurchase(Purchase purchase) {
        try {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (purchase.getSku().equals(product)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(BillingResult billingResult, String s) {
                            Toast.makeText(Billing.this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
                        }
                    };
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    //now you can purchase same product again and again
                    //Here we give coins to user.
                    tv.setText("Purchase Successful");
                    Toast.makeText(this, "Purchase Successful", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        Toast.makeText(this, "onPurchases Updated", Toast.LENGTH_SHORT).show();
    }
}

