package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import com.jobesk.kikiiapp.R;
import com.wang.avi.AVLoadingIndicatorView;

public class CustomLoader extends LinearLayout {

    private AVLoadingIndicatorView loader = null;
    private AlertDialog.Builder builder = null;
    private Activity mContext;
    private AlertDialog dialog;
    private LottieAnimationView loading;
    private TextView tv_progress;

    public CustomLoader(Activity context, boolean val) {
        super(context);
        this.mContext = context;
        loadIndicator(val);
    }

    private void loadIndicator(boolean cancelable) {

        builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = mContext.getLayoutInflater();


        View a = layoutInflater.inflate(R.layout.custom_loader, CustomLoader.this);

        loading = a.findViewById(R.id.animationView);
        tv_progress=a.findViewById(R.id.tv_progress);
        loading.setAnimation(R.raw.loading_wave);
        loading.setRepeatCount(Animation.INFINITE);

        builder.setView(a).setCancelable(cancelable);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void showIndicator() {
        loading.playAnimation();
        dialog.show();
    }

    public void hideIndicator() {
        loading.pauseAnimation();
        dialog.dismiss();
    }

    public boolean isShowing() {
        if (dialog.isShowing()) return true;
        else return false;
    }

    public void setProgress(String text) {
        tv_progress.setText(text);
    }
    public void setProgressVisibility(Boolean status) {
        if(status)tv_progress.setVisibility(VISIBLE);
        else tv_progress.setVisibility(GONE);
    }
}
