package com.jobesk.kikiiapp.Utils;

import retrofit2.Call;

public interface IApiResponseListener {
    void onApiResponseListener(Call<Object> call);
}
