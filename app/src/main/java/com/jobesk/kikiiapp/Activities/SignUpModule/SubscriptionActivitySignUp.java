package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jobesk.kikiiapp.Activities.MainActivity;
import com.jobesk.kikiiapp.Adapters.GalleryPagerAdapter;

import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.AutoScrollViewPager;
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

public class SubscriptionActivitySignUp extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, PurchasesUpdatedListener {

    private static final String TAG = "SubscriptionActivityApp";
    private Context mContext = SubscriptionActivitySignUp.this;

    public AutoScrollViewPager viewEvents;
    public LinearLayout viewDots;
    public int dotsCount;
    private ImageView[] dots;
    public GalleryPagerAdapter galleryPagerAdapter;
    private ArrayList<Integer> galleryList;

    private TextView no_thanks_tv;
    private Button subscribe_tv;
    //    private BillingProcessor billingProcess;

    private String liciencPlayConsole = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjabeTLPT69ksfHtSd03RMhLi+73KqyDR7XzlHMfyzMyqimx7at66nX+UX6Qk6Ffc89M3euB3lgrNCMNaft8W/OnqatZbCEs47PLhkDm0LFS6fUbia6R7hTiKiBxIOFlvY/x7n96Mrx4RlMCX93RPzY5Up1vLYGcyDO09xSfPK5P201WiJe8vf/DqkRbonC7Uc0mXPXUwHLPK5ViCHDWtTnm5wk5xHLJhn55kr2y2DVQMigJQBS93ZJwgO7Q3cgCuoZJPJklhb41l2uKAw2qB1eTyy1W6jADvREckUXBJ/yjTiubtZGR8VSBM09WIDrnyUOV+PgGzsZ0UzMw4NlQJGQIDAQAB";

    BillingClient billingClient;
    List<String> skulist = new ArrayList<>();
    String product = "one_month";
    private CustomLoader customLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);


        customLoader = new CustomLoader(this, false);


        initComponents();
        setViewPagerData();
        subscribe_tv.setOnClickListener(this);


        addBilling();
    }

    private void addBilling() {

        billingClient = BillingClient.newBuilder(SubscriptionActivitySignUp.this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            //This method starts when user buys a product
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        handlepurchase(purchase);
                    }
                } else {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Toast.makeText(SubscriptionActivitySignUp.this, "Try Purchasing Again", Toast.LENGTH_SHORT).show();
                    } else {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            Toast.makeText(SubscriptionActivitySignUp.this, "Already Purchased", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(SubscriptionActivitySignUp.this, "Successfully connected to Billing client", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SubscriptionActivitySignUp.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SubscriptionActivitySignUp.this, "Disconnected from the Client", Toast.LENGTH_SHORT).show();
            }
        });
        skulist.add(product);


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
                            Toast.makeText(SubscriptionActivitySignUp.this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
                        }
                    };
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    //now you can purchase same product again and again
                    //Here we give coins to user.
                    Toast.makeText(mContext, "Purchase Successful", Toast.LENGTH_SHORT).show();


                    updatePurchaseOnserver();

                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void updatePurchaseOnserver() {

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(SubscriptionActivitySignUp.this);

        Map<String, String> params = new HashMap<>();
        params.put("upgraded", "1");


        Call callbackGetProfileCall = api.updateProfileUpgrated(sessionManager.getAccessToken(), params);
        callbackGetProfileCall.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                customLoader.hideIndicator();
                Log.d("TAG", "onResponse: " + response);

                if (response != null) {
                    if (response.isSuccessful()) {
                        sessionManager.saveIsPaid(true);
                        sessionManager.saveUpgraded(1);


                        Intent intenHere = new Intent(SubscriptionActivitySignUp.this, MainActivity.class);
                        intenHere.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intenHere);

                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });
    }


    private void initComponents() {
        viewEvents = (AutoScrollViewPager) findViewById(R.id.viewEvents);
        viewDots = (LinearLayout) findViewById(R.id.viewDots);

        subscribe_tv = findViewById(R.id.btn_next);
        no_thanks_tv = findViewById(R.id.no_thanks_tv);
        no_thanks_tv.setOnClickListener(this::onClick);


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setPageViewIndicator(galleryPagerAdapter, viewEvents, viewDots, position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setViewPagerData() {
        galleryList = new ArrayList<>();

        galleryList.add(R.drawable.banner_image_1);
        galleryList.add(R.drawable.banner_image_2);
        galleryList.add(R.drawable.banner_image_1);
        galleryList.add(R.drawable.banner_image_2);
        galleryList.add(R.drawable.banner_image_1);

        galleryPagerAdapter = new GalleryPagerAdapter(this, galleryList);
        viewEvents.setAdapter(galleryPagerAdapter);
        viewEvents.setCurrentItem(0);
        viewEvents.setOnPageChangeListener(this);
        viewEvents.startAutoScroll();
        viewEvents.setInterval(5000);
        viewEvents.setCycle(true);
        viewEvents.setStopScrollWhenTouch(true);
        setPageViewIndicator(galleryPagerAdapter, viewEvents, viewDots);
    }

    private void setPageViewIndicator(final GalleryPagerAdapter galleryPagerAdapter, final AutoScrollViewPager viewEvents, final LinearLayout viewDots) {
        viewDots.removeAllViews();
        try {
            Log.d("###setPageViewIndicator", " : called");
            dotsCount = galleryPagerAdapter.getCount();
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        20,
                        20
                );
                params.setMargins(10, 0, 10, 0);

                final int presentPosition = i;
                dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        viewEvents.setCurrentItem(presentPosition);
                        return true;
                    }

                });
                viewDots.addView(dots[i], params);
            }
            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPageViewIndicator(final GalleryPagerAdapter galleryPagerAdapter, final AutoScrollViewPager viewEvents, final LinearLayout viewDots, int position) {
        viewDots.removeAllViews();
        try {
            Log.d("###setPageViewIndicator", " : called");
            dotsCount = galleryPagerAdapter.getCount();
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        20,
                        20
                );
                params.setMargins(10, 0, 10, 0);

                final int presentPosition = i;
                dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        viewEvents.setCurrentItem(presentPosition);
                        return true;
                    }

                });
                viewDots.addView(dots[i], params);
            }
            if (dots.length > 0) {
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_thanks_tv:

                Intent loginIntent = new Intent(mContext, MainActivity.class);
                TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();


                finish();
                break;
            case R.id.btn_next:
                final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skulist).setType(BillingClient.SkuType.SUBS);  //Skutype.subs for Subscription
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
                                BillingResult responsecode = billingClient.launchBillingFlow(SubscriptionActivitySignUp.this, flowParams);
                            }
                        }
                    }
                });
                break;
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Toast.makeText(this, "onPurchases Updated", Toast.LENGTH_SHORT).show();
    }
}
