package com.jobesk.kikiiapp.Activities.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.ChipAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetCategory;
import com.jobesk.kikiiapp.Model.ChipModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.ChipViewAdapter;
import com.plumillonforge.android.chipview.OnChipClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoYouDrinkActivity extends AppCompatActivity implements OnChipClickListener, View.OnClickListener {

    private static final String TAG = "DoYouDrinkActivity";
    private Context context = DoYouDrinkActivity.this;

    private List<Chip> chipList = new ArrayList<>();
    private ChipViewAdapter chipViewAdapter;
    private ChipView chip_statuses;
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetCategory> callbackGetCategoryCall;
    private CallbackGetCategory responseGetCategory;
    private String isChecked = "null";
    private ImageView img_back;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_you_drink);

        initComponents();
        getIdentity();

        chip_statuses.setOnChipClickListener(this);
        img_back.setOnClickListener(this);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DRINK, isChecked);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }


    @Override
    public void onChipClick(Chip chip) {
        isChecked = chip.getText();
        chipViewAdapter = new ChipAdapter(context, isChecked);
        chip_statuses.setAdapter(chipViewAdapter);
        chip_statuses.setChipList(chipList);
        //Toast.makeText(this, "chip clicked", Toast.LENGTH_SHORT).show();
    }

    private void getIdentity() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetCategoryCall = api.getCategory(sessionManager.getAccessToken(), Constants.DRINK);
        callbackGetCategoryCall.enqueue(new Callback<CallbackGetCategory>() {
            @Override
            public void onResponse(Call<CallbackGetCategory> call, Response<CallbackGetCategory> response) {
                Log.d(TAG, "onResponse: " + response);
                customLoader.hideIndicator();
                responseGetCategory = response.body();

                Log.d("cateGoriesHere", "onResponse: " + new Gson().toJson(responseGetCategory));

                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {

                        if (responseGetCategory.getValue() != null) {


                            try {
                                if (responseGetCategory.getValue().getValueAttr().size() > 0) {

                                    setData();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetCategory.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetCategory.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetCategory> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData() {

        int val = responseGetCategory.getValue().getValueAttr().size();
        for (int i = 0; i < responseGetCategory.getValue().getValueAttr().size(); i++) {
            Log.d(TAG, "setData: " + responseGetCategory.getValue().getValueAttr().get(i));
            chipList.add(new ChipModel(responseGetCategory.getValue().getValueAttr().get(i)));
        }
        if (responseGetCategory.getIsChecked() != null)
            chipViewAdapter = new ChipAdapter(context, responseGetCategory.getIsChecked());
        else
            chipViewAdapter = new ChipAdapter(context, "null");
        chip_statuses.setAdapter(chipViewAdapter);
        chip_statuses.setChipList(chipList);
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        chip_statuses = findViewById(R.id.chip_statuses);

        img_back = findViewById(R.id.img_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
        }
    }
}
