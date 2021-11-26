package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonApis {
    private static final String TAG = "CommonApis";
    private static CustomLoader customLoader;
    private static SessionManager sessionManager;

    private static Call<CallbackUpdateProfile> callbackUpdateCategory;
    private static CallbackUpdateProfile responseUpdateCategory;

    private static void updateCategory(final Activity activity,Map<String, String> updateCategoryParam) {
        customLoader=new CustomLoader(activity,false);
        sessionManager=new SessionManager(activity);

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(activity);
        Log.d(TAG, "updateCategory: " + sessionManager.getAccessToken());
        callbackUpdateCategory = api.updateProfile(sessionManager.getAccessToken(), updateCategoryParam);
        callbackUpdateCategory.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUpdateCategory = response.body();
                if (responseUpdateCategory != null) {
                    if (responseUpdateCategory.getSuccess()) {
                        customLoader.hideIndicator();
                        activity.setResult(Activity.RESULT_OK);
                        activity.onBackPressed();
                    } else {
                        Log.d(TAG, "onResponse: " + responseUpdateCategory.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(activity, responseUpdateCategory.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(activity);
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
}
