package com.jobesk.kikiiapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.gson.Gson;
import com.jobesk.kikiiapp.Activities.Profile.SetHeightActivity;
import com.jobesk.kikiiapp.Adapters.PremiumFiltersAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetFilters;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.Filters;
import com.jobesk.kikiiapp.Model.filters.Filter;
import com.jobesk.kikiiapp.Model.filters.FilterModelShow;
import com.jobesk.kikiiapp.Model.filters.FiltersModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
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
import retrofit2.Retrofit;

public class FiltersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FiltersActivity";
    private Context context = FiltersActivity.this;
    public static final int REQUEST_SET_HEIGHT = 220;
    private RecyclerView rv_premium_filters;
    private PremiumFiltersAdapter premiumFiltersAdapter;
    private List<FilterModelShow> premiumFilterList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private CrystalRangeSeekbar rsb_age;
    private CrystalSeekbar sb_distance;
    private TextView tv_start_age, tv_end_age, tv_distance_value, tv_kms, tv_miles, tv_height, tv_height_value, tv_any_age;
    private double currentDistance;
    private ImageView img_ok;
    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private Call<CallbackStatus> callbackUpdateFilters;
    private CallbackStatus responseUpdateFilters;
    private Call<CallbackGetFilters> callbackGetFiltersCall;
    private CallbackGetFilters responseGetFilters;
    private Map<String, String> updateFiltersParam = new HashMap<>();
    private String height = "", distanceIn = "km";
    private ImageView img_back;
    private TextView clear_tv, upgrade_tv;
    private boolean fromMax = false, fromMin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        initComponents();

        tv_height.setOnClickListener(this);
        tv_any_age.setOnClickListener(this);
        tv_kms.setOnClickListener(this);
        tv_miles.setOnClickListener(this);
        img_ok.setOnClickListener(this);
        img_back.setOnClickListener(this);

        rsb_age.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                tv_end_age.setText(String.valueOf(maxValue));
                tv_start_age.setText(String.valueOf(minValue));

            }
        });

        // set final value listener
        rsb_age.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                tv_start_age.setText(String.valueOf(minValue));
                tv_end_age.setText(String.valueOf(maxValue));
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        sb_distance.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                currentDistance = Double.parseDouble(String.valueOf(value));
                tv_distance_value.setText(String.valueOf(value));
            }
        });

        boolean isPaid = sessionManager.getIsPaid();
        gridLayoutManager = new GridLayoutManager(context, 2);
        rv_premium_filters.setLayoutManager(gridLayoutManager);
        premiumFiltersAdapter = new PremiumFiltersAdapter(premiumFilterList, FiltersActivity.this);
        rv_premium_filters.setAdapter(premiumFiltersAdapter);

        if (isPaid) {
            getAvaliableFilters();
            rv_premium_filters.setVisibility(View.VISIBLE);
            upgrade_tv.setVisibility(View.GONE);
        } else {
            rv_premium_filters.setVisibility(View.GONE);
            upgrade_tv.setVisibility(View.VISIBLE);
        }
    }
    private void getAvaliableFilters() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        Call<FiltersModel> Call = api.availableFilters(sessionManager.getAccessToken());
        Call.enqueue(new Callback<FiltersModel>() {
            @Override
            public void onResponse(Call<FiltersModel> call, Response<FiltersModel> response) {
                customLoader.hideIndicator();
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                FiltersModel filtersModel = response.body();
                if (filtersModel != null) {
                    if (response.isSuccessful()) {
                        List<Filter> filtersList = filtersModel.getFilters();
                        for (int i = 0; i < filtersList.size(); i++) {
                            String key = filtersList.get(i).getKey();
                            Log.d("KeyValueHere", "onResponse: " + key);
                            List<String> value = filtersList.get(i).getValue();
                            FilterModelShow modelShow = new FilterModelShow();
                            modelShow.setSelected(false);
                            modelShow.setValue(value);
                            modelShow.setTitleUnchangeBle(key);
                            String here = returnTheTitles(key);
                            modelShow.setTitle(here);

                            premiumFilterList.add(modelShow);
                        }


//                        gridLayoutManager = new GridLayoutManager(context, 2);
//                        rv_premium_filters.setLayoutManager(gridLayoutManager);
//                        rv_premium_filters.setAdapter(new PremiumFiltersAdapter(premiumFilterList, FiltersActivity.this));

                        premiumFiltersAdapter.notifyDataSetChanged();
                        getFilters();

                    } else {
//                        Log.d(TAG, "onResponse: " + responseUpdateFilters.getMessage());
                        customLoader.hideIndicator();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<FiltersModel> call, Throwable t) {
                customLoader.hideIndicator();
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                }
            }
        });
    }

    private String returnTheTitles(String value) {

        switch (value) {
            case "gender_identity":
                return "Gender Identity";

            case "sexual_identity":
                return "Sexual Identity";

            case "pronouns":
                return "pronouns";

            case "relationship_status":
                return "Relationship Status";

            case "diet_like":
                return "Diet Like";

            case "sign":
                return "Sign";

            case "looking_for":
                return "Looking for";

            case "drink":
                return "Drink";

            case "cannabis":
                return "Cannabis";

            case "political_views":
                return "Political views";

            case "religion":
                return "Religion";

            case "pets":
                return "Pets";

            case "kids":
                return "Kids";

            case "smoke":
                return "Smoke";

            case "last_online":
                return "Last Online";
        }
        return "";
    }

    private void getFilters() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetFiltersCall = api.getFilters(sessionManager.getAccessToken());
        callbackGetFiltersCall.enqueue(new Callback<CallbackGetFilters>() {
            @Override
            public void onResponse(Call<CallbackGetFilters> call, Response<CallbackGetFilters> response) {
                Log.d(TAG, "onResponse: " + new Gson().toJson(response.body()));
                responseGetFilters = response.body();
                if (responseGetFilters != null) {
                    if (responseGetFilters.getSuccess()) {
                        customLoader.hideIndicator();
                        if (responseGetFilters.getFilters() != null) {

                            setData(responseGetFilters.getFilters());

                        }
                    } else {
                        Log.d(TAG, "onResponse: " + responseUpdateFilters.getMessage());
                        customLoader.hideIndicator();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetFilters> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void setData(Filters filters) {


        for (int i = 0; i < premiumFilterList.size(); i++) {

            if (filters.getGenderIdentity() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("gender_identity")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getGenderIdentity()));
                }
            }
            if (filters.getSexualIdentity() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("sexual_identity")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getSexualIdentity()));
                }
            }
            if (filters.getPronouns() != null) {

                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("pronouns")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getPronouns()));


                }
            }
            if (filters.getRelationshipStatus() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("relationship_status")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getRelationshipStatus()));

                }
            }
            if (filters.getDietLike() != null) {

                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("diet_like")) {

                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getDietLike()));
                }
            }
            if (filters.getSign() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("sign")) {

                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getSign()));
                }
            }
            if (filters.getLookingFor() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("looking_for")) {

                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getLookingFor()));
                }
            }
            if (filters.getDrink() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("drink")) {

                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getDrink()));
                }
            }
            if (filters.getCannabis() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("cannabis")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getCannabis()));
                }
            }
            if (filters.getPoliticalViews() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("political_views")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getPoliticalViews()));
                }
            }
            if (filters.getReligion() != null) {

                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("religion")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getReligion()));
                }
            }
            if (filters.getPets() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("pets")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getPets()));
                }
            }
            if (filters.getKids() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("kids")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getKids()));
                }
            }
            if (filters.getSmoke() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("smoke")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getSmoke()));
                }
            }
            if (filters.getLastOnline() != null) {
                if (premiumFilterList.get(i).getTitleUnchangeBle().equalsIgnoreCase("last_online")) {
                    premiumFilterList.get(i).setSelected(true);
                    premiumFilterList.get(i).setTitle(String.valueOf(filters.getLastOnline()));
                }
            }
        }


        premiumFiltersAdapter.notifyDataSetChanged();
//        distance
//                distance_in
//                to_age


        if (responseGetFilters.getFilters().getFromAge() != null) {
            fromMax = true;
            fromMin = false;
            int rangeHere = Integer.parseInt(responseGetFilters.getFilters().getFromAge());

//            tv_start_age.setText(String.valueOf(rangeHere));
//            rsb_age.setMinValue(16).apply();
//            rsb_age.setMinStartValue(rangeHere).apply();
            int valueHere = Integer.parseInt(responseGetFilters.getFilters().getToAge());

            rsb_age
//                    .setMaxValue(60)
                    .setMaxStartValue(valueHere)
//                    .setMinValue(18)
                    .setMinStartValue(rangeHere)
                    .apply();

            Log.d(TAG, "setData: ");

        }
//        if (responseGetFilters.getFilters().getToAge() != null) {
//
//            fromMax = false;
//            fromMin = true;
//
//            int valueHere = Integer.parseInt(responseGetFilters.getFilters().getToAge());
//            tv_end_age.setText(String.valueOf(valueHere));
//
//            rsb_age.setMaxValue(60).apply();
//            rsb_age.setMaxStartValue(valueHere).apply();
//        }
        if (responseGetFilters.getFilters().getDistance() != null) {

            tv_distance_value.setText(responseGetFilters.getFilters().getDistance());
            int value = Integer.parseInt(responseGetFilters.getFilters().getDistance());

            sb_distance
//                    .setMaxValue(60)
//                    .setMaxStartValue(valueHere)
//                    .setMinValue(18)
                    .setMinStartValue(value)
                    .apply();
            Log.d(TAG, "setData: ");
//            sb_distance.setMinStartValue(value);

        }
        if (responseGetFilters.getFilters().getDistanceIn() != null) {
            if (responseGetFilters.getFilters().getDistanceIn().equalsIgnoreCase("mi")) {
                tv_kms.setTextColor(getResources().getColor(R.color.grey));
                tv_miles.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                distanceIn = "mi";
            } else {
                tv_kms.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_miles.setTextColor(getResources().getColor(R.color.grey));
                distanceIn = "km";
            }
        }
        if (responseGetFilters.getFilters().getHeight() != null)
            tv_height_value.setText(responseGetFilters.getFilters().getHeight());

    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);
        rv_premium_filters = findViewById(R.id.rv_premium_filters);
        rsb_age = findViewById(R.id.rsb_age);
        sb_distance = findViewById(R.id.sb_distance);
        tv_start_age = findViewById(R.id.tv_start_age);
        tv_end_age = findViewById(R.id.tv_end_age);
        tv_distance_value = findViewById(R.id.tv_distance_value);
        tv_miles = findViewById(R.id.tv_miles);
        tv_kms = findViewById(R.id.tv_kms);
        tv_height = findViewById(R.id.tv_height);
        tv_height_value = findViewById(R.id.tv_height_value);
        tv_any_age = findViewById(R.id.tv_any_age);
        img_ok = findViewById(R.id.img_ok);
        img_back = findViewById(R.id.img_back);
        upgrade_tv = findViewById(R.id.upgrade_tv);
        clear_tv = findViewById(R.id.clear_tv);

        clear_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (premiumFilterList.size() > 0) {
                    for (int i = 0; i < premiumFilterList.size(); i++) {
                        premiumFilterList.get(i).setSelected(false);
                        premiumFilterList.get(i).setTitle(premiumFilterList.get(i).getTitleUnchangeBle());
                    }
                    premiumFiltersAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_HEIGHT && resultCode == RESULT_OK) {
            if (data != null) {
                String value = data.getStringExtra(Constants.HEIGHT);
                tv_height_value.setText(value);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_any_age:
                tv_height_value.setText("Any Height");
                break;
            case R.id.tv_height:
                startActivityForResult(new Intent(context, SetHeightActivity.class), REQUEST_SET_HEIGHT);
                break;
            case R.id.tv_kms:
                tv_kms.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tv_miles.setTextColor(getResources().getColor(R.color.grey));
                distanceIn = "km";
                break;
            case R.id.tv_miles:
                tv_kms.setTextColor(getResources().getColor(R.color.grey));
                tv_miles.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                distanceIn = "mi";
                break;
            case R.id.img_ok:
                if (tv_height_value.getText().toString().equalsIgnoreCase("Any Height"))
                    height = "";
                else
                    height = tv_height_value.getText().toString();
                for (int i = 0; i < premiumFilterList.size(); i++) {
                    if (premiumFilterList.get(i).isSelected()) {

                        updateFiltersParam.put(premiumFilterList.get(i).getTitleUnchangeBle(), premiumFilterList.get(i).getTitle());

                    } else {
                        updateFiltersParam.put(premiumFilterList.get(i).getTitleUnchangeBle(), "");
                    }
                }
                updateFiltersParam.put(Constants.FROM_AGE, tv_start_age.getText().toString());
                updateFiltersParam.put(Constants.TO_AGE, tv_end_age.getText().toString());
                updateFiltersParam.put(Constants.DISTANCE, tv_distance_value.getText().toString());
                updateFiltersParam.put(Constants.HEIGHT, height);
                updateFiltersParam.put(Constants.DISTANCE_IN, distanceIn);
                updateFilters();
                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    private void updateFilters() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackUpdateFilters = api.updateFilters(sessionManager.getAccessToken(), updateFiltersParam);
        callbackUpdateFilters.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUpdateFilters = response.body();
                if (responseUpdateFilters != null) {
                    if (responseUpdateFilters.getSuccess()) {
                        customLoader.hideIndicator();
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE", "true");
                        setResult(1, intent);
                        finish();
                    } else {

                        Log.d(TAG, "onResponse: " + responseUpdateFilters.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseUpdateFilters.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackStatus> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }
}
