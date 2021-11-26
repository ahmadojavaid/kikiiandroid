package com.jobesk.kikiiapp.Netwrok;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.jobesk.kikiiapp.Utils.ShowDialogues;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {

    public static API createAPI(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.addInterceptor(new NetworkConnectionInterceptor(context));
//        if(BuildConfig.DEBUG){
//            builder.addInterceptor(logging);
//        }
        builder.cache(null);
        OkHttpClient okHttpClient = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEB_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(API.class);
    }
    public static API createAPI(Context context,String OAuth) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.addInterceptor(new NetworkConnectionInterceptor(context));
//        if(BuildConfig.DEBUG){
//            builder.addInterceptor(logging);
//        }
        builder.cache(null);
        OkHttpClient okHttpClient = builder.build();

        if(OAuth.equalsIgnoreCase("fields")){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.INSTAGRAM_FIELDS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            return retrofit.create(API.class);
        }
        else{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.INSTAGRAM_ACCESS_TOKEN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            return retrofit.create(API.class);
        }
    }
    public static class NetworkConnectionInterceptor implements Interceptor {

        private Context mContext;

        public NetworkConnectionInterceptor(Context context) {
            mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            if (!isConnected()) {
                throw new NoConnectivityException();
                // Throwing our custom exception 'NoConnectivityException'
            }

            Request.Builder builder = chain.request().newBuilder();
            return chain.proceed(builder.build());
        }

        public boolean isConnected(){
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }
        public class NoConnectivityException extends IOException {
            @Override
            public String getMessage() {
                Log.d("apii", "getMessage: No Internet Connection");
                ShowDialogues.SHOW_NO_INTERNET_DIALOG(mContext);
                return "No Internet Connection";
                // You can send any message whatever you want from here.
            }
        }
    }
}
