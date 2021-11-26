package com.jobesk.kikiiapp.Activities.SignUpModule;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.jobesk.kikiiapp.Netwrok.Constants;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.SessionManager;

public class InstagramLoginWebView extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.acitivity_instagram_login_webview);
        webView = (WebView) findViewById(R.id.game_web_view);
        progressBar = (ProgressBar) findViewById(R.id.game_progress_bar);
        webView.setVisibility(View.GONE);
        sessionManager = new SessionManager(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                if (url.contains("code=")) {
                    Uri uri = Uri.EMPTY.parse(url);
                    //String access_token = uri.getEncodedFragment();
                    String access_token = uri.toString();
                    access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                    Log.d("urllls", "onPageFinished: "+url);
                    access_token=access_token.substring(0, access_token.length() - 2);
                    Intent intent=new Intent();
                    intent.putExtra(Constants.TOKEN,access_token);
                    setResult(RESULT_OK,intent);
                    finish();
                    //listener.onTokenReceived(access_token);
                }
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                if (appInstalledOrNot(url)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    // do something if app is not installed
                }
                return true;
            }
        });
        webView.loadUrl(getIntent().getStringExtra("url"));
        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }

        });

    }

    private void webViewGoBack() {
        webView.goBack();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
               /* Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();*/
              /* sessionManager.logoutUser();
               startActivity(new Intent(StripeWebView.this,Login.class));
               finish();*/
            setResult(RESULT_OK);
            super.onBackPressed();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
