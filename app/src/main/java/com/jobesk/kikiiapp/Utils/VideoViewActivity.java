package com.jobesk.kikiiapp.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.jobesk.kikiiapp.R;
import com.potyvideo.library.AndExoPlayerView;

public class VideoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        Intent intent = getIntent();
        String videoLink = intent.getStringExtra("videoLink");
        String from = intent.getStringExtra("from");

        ImageView
                back_img = findViewById(R.id.back_img);
        AndExoPlayerView andExoPlayerView = findViewById(R.id.andExoPlayerView);
        WebView webView = findViewById(R.id.webView);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (from != null) {
            andExoPlayerView.setVisibility(View.GONE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(videoLink);
        } else {
            webView.setVisibility(View.GONE);
            andExoPlayerView.setSource(videoLink);
        }
    }
}