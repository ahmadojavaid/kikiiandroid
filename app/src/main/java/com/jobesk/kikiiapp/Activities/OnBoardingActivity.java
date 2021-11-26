package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jobesk.kikiiapp.Activities.SignUpModule.SignUpActivity;
import com.jobesk.kikiiapp.Adapters.OnBoardPagerAdapter;

import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Model.introImages.Image;
import com.jobesk.kikiiapp.Model.introImages.IntroImagesModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OnBoardingActivity";
    private Context mContext = OnBoardingActivity.this;

    private ViewPager vp_on_boarding;
    private LinearLayout ll_dots;
    private TextView[] mDots;
    private TextView tv_skip;

    private OnBoardPagerAdapter pagerAdapter;
    private CustomLoader customLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        initComponents();


        tv_skip.setOnClickListener(this);


        customLoader = new CustomLoader(this, false);

        getIntroScreens();
    }

    private void getIntroScreens() {

        API api = RestAdapter.createAPI(OnBoardingActivity.this);
        Call callback = api.getAllIntroImages();
        callback.enqueue(new Callback<IntroImagesModel>() {
            @Override
            public void onResponse(Call<IntroImagesModel> call, Response<IntroImagesModel> response) {
                Log.d(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    IntroImagesModel model = response.body();
                    List<Image> imagesList = model.getImages();

                    addDotsIndicator(0);
                    vp_on_boarding.addOnPageChangeListener(viewPagerListener);
                    pagerAdapter = new OnBoardPagerAdapter(getApplicationContext(),imagesList);
                    vp_on_boarding.setAdapter(pagerAdapter);
                }

            }

            @Override
            public void onFailure(Call<IntroImagesModel> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });
    }


    private void initComponents() {
        customLoader = new CustomLoader(this, false);

        vp_on_boarding = findViewById(R.id.vp_on_boarding);
        ll_dots = findViewById(R.id.ll_dots);


        tv_skip = findViewById(R.id.tv_skip);
    }

    private void addDotsIndicator(int position) {
        mDots = new TextView[4];
        ll_dots.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.light_grey));
            ll_dots.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View v) {
        startActivity(new Intent(mContext, SignUpActivity.class));
        finish();
    }
}
