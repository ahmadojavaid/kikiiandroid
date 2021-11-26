package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.jobesk.kikiiapp.Activities.SignUpModule.SignUpActivity;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.SessionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        sessionManager = new SessionManager(this);

        //    printKeyHash();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sessionManager.checkLogin()) {
                    if (sessionManager.getFirstAttempt()) {
                        startActivity(new Intent(SplashActivity.this, OnBoardingActivity.class));
                        sessionManager.saveFirstAttempt(false);
                    } else {
                        sessionManager.saveFirstAttempt(false);
                        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                    }
                } else {

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if (getIntent().getStringExtra("type") == null) {
                        startActivity(intent);
                    } else {

                        String notiType = getIntent().getStringExtra("type");
                        if (notiType.equalsIgnoreCase("match")) {
                            String image = getIntent().getStringExtra("otherUserImage");
                            String id = getIntent().getStringExtra("otherUserID");
                            intent.putExtra("otherUserImage", image);
                            intent.putExtra("otherUserID", id);
                            String senderName = getIntent().getStringExtra("sender_name");
                            String senderImage = getIntent().getStringExtra("sender_image");
                            String receiverName = getIntent().getStringExtra("receiver_name");
                            String reveiverImage = getIntent().getStringExtra("receiver_image");
                            intent.putExtra("receiverName", receiverName);
                            intent.putExtra("receiverImage", reveiverImage);
                            intent.putExtra("senderName", senderName);
                            intent.putExtra("senderImage", senderImage);
                            intent.putExtra("type", notiType);
                            startActivity(intent);
                        }


                        if (notiType.equalsIgnoreCase("post_like")) {

                            String postID = getIntent().getStringExtra("postID");
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("postID", postID);
                            intent.putExtra("type", notiType);
                            startActivity(intent);

                        }

                        if (notiType.equalsIgnoreCase("post_comment")) {

                            String postID = getIntent().getStringExtra("postID");
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("postID", postID);
                            intent.putExtra("type", notiType);
                            startActivity(intent);

                        }
                        if (notiType.equalsIgnoreCase("comment_reply")) {

                            String postID = getIntent().getStringExtra("postID");
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("postID", postID);
                            intent.putExtra("type", notiType);
                            startActivity(intent);

                        }


                    }

                }
                finish();
            }
        }, 3000);
    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
