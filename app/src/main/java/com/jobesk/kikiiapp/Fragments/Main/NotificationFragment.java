package com.jobesk.kikiiapp.Fragments.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.NotificationAdapter;
import com.jobesk.kikiiapp.Model.notificationModel.Notification;
import com.jobesk.kikiiapp.Model.notificationModel.NotificationModel;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;
    private NotificationAdapter notificationAdapter;
    private List<String> notificationList = new ArrayList<>();
    private RecyclerView rv_notification;
    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private ImageView img_add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        initComponents(view);
        getAllnotifications();

        return view;
    }

    private void initComponents(View view) {

        context = getContext();
        customLoader = new CustomLoader(getActivity(), false);
        sessionManager = new SessionManager(getActivity());
        rv_notification = view.findViewById(R.id.rv_notification);
        img_add = view.findViewById(R.id.img_add);
        img_add.setVisibility(View.GONE);

    }

    private void getAllnotifications() {

        customLoader.showIndicator();
        API api = RestAdapter.createAPI(getActivity());

        Call callback = api.getNotifications(sessionManager.getAccessToken());
        callback.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                Log.d("reportsubmit", "onResponse: " + new Gson().toJson(response.body()));
                customLoader.hideIndicator();
                if (response.isSuccessful()) {

                    NotificationModel model = response.body();
                    List<Notification> listData = model.getNotifications();


                    NotificationAdapter adapter = new NotificationAdapter(listData, context);
                    rv_notification.setLayoutManager(new LinearLayoutManager(context));
                    rv_notification.setItemAnimator(new DefaultItemAnimator());
                    rv_notification.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<NotificationModel> call, Throwable t) {
                customLoader.hideIndicator();
                Log.d("TAG", "onResponse: " + t.getMessage());
            }
        });
    }
}
