package com.jobesk.kikiiapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;

public class TermsAndConditionsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TermsAndConditionsActiv";
    private Context context=TermsAndConditionsActivity.this;
    private Activity activity=TermsAndConditionsActivity.this;

    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        initComponents();

        img_back.setOnClickListener(this);
    }

    private void initComponents() {
        img_back=findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }
}
