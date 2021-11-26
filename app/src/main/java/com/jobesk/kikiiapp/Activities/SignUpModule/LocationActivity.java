package com.jobesk.kikiiapp.Activities.SignUpModule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationActivity";
    private Context mContext = LocationActivity.this;
    private Button btn_enable_location;

    private GoogleApiClient googleApiClient;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private SessionManager sessionManager;
    private CustomLoader customLoader;
    private Call<CallbackUpdateProfile> callbackStatusCall;
    private CallbackUpdateProfile responseLatLongUpdate;
    private Map<String, String> updateLocationParams = new HashMap<>();
    private Double latitude, longitude;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initComponents();
        btn_enable_location.setOnClickListener(this);
    }

    private void initComponents() {
        sessionManager = new SessionManager(this);

        customLoader = new CustomLoader(this, false);

        btn_enable_location = findViewById(R.id.btn_enable_location);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enable_location:
                setUpGClient();
                checkPermissions();
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            customLoader.showIndicator();
            getMyLocation();
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, LocationActivity.this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback() {

                        @Override
                        public void onResult(@NonNull Result result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(mContext,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult((Activity) mContext,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    //finish();
                                    break;
                            }
                        }


                    });
                }
            }
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        customLoader.showIndicator();
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                }
                break;
        }
        int permissionLocation = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mylocation = location;
            if (mylocation != null) {
                latitude = mylocation.getLatitude();
                longitude = mylocation.getLongitude();

                sessionManager.saveLat(String.valueOf(latitude));
                sessionManager.saveLng(String.valueOf(longitude));

                //updateLocation();
                Log.d("TAG", "onLocationChanged:LATITUDE " + latitude);
                Log.d("TAG", "onLocationChanged:LONGITUDE " + longitude);

                if (!sessionManager.getLat().isEmpty()
                        && !sessionManager.getLng().isEmpty()) {
                    updateLocationParams.put(Constants.LATITUDE, sessionManager.getLat());
                    updateLocationParams.put(Constants.LONGITUDE, sessionManager.getLng());

                    if (count % 10 == 0)
                        updateLocation();
                    else
                        count++;
                    //customLoader.hideIndicator();

                    //startActivity(new Intent(mContext,RegistrationActivity.class));
                }
            }
        } catch (Exception e) {
            Log.d("TAG", "onLocationChanged: " + e.getMessage());
        }
    }

    public void updateLocation() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(mContext);
        Log.d(TAG, "sendOTP: " + updateLocationParams.size());
        Log.d(TAG, "token: " + sessionManager.getAccessToken());
        callbackStatusCall = api.updateProfile(sessionManager.getAccessToken(), updateLocationParams);
        callbackStatusCall.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseLatLongUpdate = response.body();
                if (responseLatLongUpdate != null) {
                    if (responseLatLongUpdate.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseLatLongUpdate.getMessage());
                        customLoader.hideIndicator();
                        goToNextActivity();
                    } else {
                        Log.d(TAG, "onResponse: " + responseLatLongUpdate.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(mContext, responseLatLongUpdate.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(mContext);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }

    private void goToNextActivity() {
        Toast.makeText(mContext, responseLatLongUpdate.getMessage(), Toast.LENGTH_SHORT).show();
        Intent loginIntent;
        if (sessionManager.getFbLogin()) {
            sessionManager.saveUserEmail(responseLatLongUpdate.getUser().getEmail());
            sessionManager.saveUserName(responseLatLongUpdate.getUser().getName());
            sessionManager.saveUserID(responseLatLongUpdate.getUser().getId().toString());
            sessionManager.createLoginSession("Bearer " + responseLatLongUpdate.getUser().getAuthToken(), responseLatLongUpdate.getUser().getId().toString());
            loginIntent = new Intent(mContext, RegistrationActivity.class);
            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
            updateLocationParams.clear();
        } else if (sessionManager.getInstaLogin()) {
            sessionManager.saveUserName(responseLatLongUpdate.getUser().getName());
            sessionManager.saveUserID(responseLatLongUpdate.getUser().getId().toString());
            sessionManager.createLoginSession("Bearer " + responseLatLongUpdate.getUser().getAuthToken(), responseLatLongUpdate.getUser().getId().toString());
            loginIntent = new Intent(mContext, RegistrationActivity.class);
            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
            updateLocationParams.clear();
        } else {
            loginIntent = new Intent(mContext, RegistrationActivity.class);
            TaskStackBuilder.create(mContext).addNextIntentWithParentStack(loginIntent).startActivities();
            updateLocationParams.clear();
        }
    }

}
