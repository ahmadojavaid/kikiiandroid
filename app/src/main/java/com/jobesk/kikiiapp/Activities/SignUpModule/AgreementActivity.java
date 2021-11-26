package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AgreementActivity";
    private Context mContext = AgreementActivity.this;

    private Button btn_next;
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        initComponents();

        btn_next.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private void initComponents() {
        btn_next = findViewById(R.id.btn_next);
        img_back = findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                Intent intent = new Intent(mContext, SubscriptionActivitySignUp.class);
                intent.putExtra("from", "a");
                startActivity(intent);
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }
}
