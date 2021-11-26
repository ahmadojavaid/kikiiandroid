package com.jobesk.kikiiapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.R;


public class HandelDeepLinkActivity extends AppCompatActivity {

    private static final String TAG = "HandelDeepLinkActivity";
    private String post_id, event_id, user_id;
    private Context context=HandelDeepLinkActivity.this;
    private Activity activity=HandelDeepLinkActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handel_deep_link);




    }

//    private void checkForDeepLink() {
//        //////////// Receiving Deep Link
//        FirebaseDynamicLinks.getInstance()
//                .getDynamicLink(getIntent())
//                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                    @Override
//                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                        // Get deep link from result (may be null if no link is found)
//                        Uri deepLink = null;
//                        if (pendingDynamicLinkData != null) {
//                            deepLink = pendingDynamicLinkData.getLink();
//                            post_id = deepLink.getQueryParameter("post_id");
//                            Log.d(TAG, "onSuccess: " + post_id);
//                            if (post_id != null) {
//                                Intent intent = new Intent(mContext, SinglePostDetailActivity.class);
//                                intent.putExtra(Constants.ID, String.valueOf(post_id));
//                                startActivity(intent);
//                            } else {
//                                event_id = deepLink.getQueryParameter("event_id");
//                                if (event_id != null) {
//
//                                } else {
//                                    user_id = deepLink.getQueryParameter("user_id");
//                                    if (user_id != null) {
//                                        Intent intent = new Intent(mContext, UserProfileActivity.class);
//                                        intent.putExtra(Constants.ID, String.valueOf(user_id));
//                                        startActivity(intent);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("dynamic link tag", "getDynamicLink:onFailure", e);
//                    }
//                });
//
//    }

}