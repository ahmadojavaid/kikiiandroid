package com.jobesk.kikiiapp.Fragments.Others;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jobesk.kikiiapp.Adapters.EventsAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetEvents;
import com.jobesk.kikiiapp.Callbacks.CallbackStatus;
import com.jobesk.kikiiapp.Model.Event;
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

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "EventsFragment";
    private Context context;
    private Activity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_no;

    private RecyclerView rv_events;
    private EventsAdapter eventsAdapter;
    private LinearLayoutManager layoutManager;
    private List<Event> eventsList = new ArrayList<>();

    private Map<String, String> eventParams = new HashMap<>();
    private CustomLoader customLoader;
    private SessionManager sessionManager;

    private Call<CallbackGetEvents> callbackGetEventsCall;
    private CallbackGetEvents responseGetEvents;

    private Call<CallbackStatus> callbackAttendEvent, callbackDeletePost;
    private CallbackStatus responseAttendEvent, responseDeletePost;

    /*****/
    ProgressBar progressBar;

    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 0;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    private int TOTAL_PAGES = 3;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;

    /*****/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);

        initComponents(view);

        loadEvents();

        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    private void loadEvents() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackGetEventsCall = api.getEvents(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetEventsCall.enqueue(new Callback<CallbackGetEvents>() {
            @Override
            public void onResponse(Call<CallbackGetEvents> call, Response<CallbackGetEvents> response) {
                Log.d(TAG, "onResponse: " + response);
                responseGetEvents = response.body();
                if (responseGetEvents != null) {
                    if (response.isSuccessful()) {
                        customLoader.hideIndicator();
                        swipeRefreshLayout.setRefreshing(false);
                        if (responseGetEvents.getEvents().size() > 0){
                            tv_no.setVisibility(View.GONE);
                            rv_events.setVisibility(View.VISIBLE);
                            setData();
                        }
                        else{
                            tv_no.setVisibility(View.VISIBLE);
                            rv_events.setVisibility(View.GONE);
                        }
                        /*else
                            currentPage = -1;*/
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetEvents.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetEvents.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetEvents> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }
    private void attendEvent() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackAttendEvent = api.attendEvent(sessionManager.getAccessToken(), eventParams);
        callbackAttendEvent.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseAttendEvent = response.body();
                if (responseAttendEvent != null) {
                    if (responseAttendEvent.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: " + responseAttendEvent.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                        eventParams.clear();
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

    private void setData() {
        currentPage = responseGetEvents.getNextOffset();
        eventsList = responseGetEvents.getEvents();
        eventsAdapter.addAll(eventsList);
        rv_events.setAdapter(eventsAdapter);

        eventsAdapter.setOnClickListeners(new EventsAdapter.IClicks() {
            @Override
            public void attendEventClick(View view, Event event, int position) {
                eventParams.put(Constants.ID,event.getId().toString());
                attendEvent();
            }

            @Override
            public void cancelEventClick(View view, Event event, int position) {
                eventParams.put(Constants.ID,event.getId().toString());
                cancelAttendEvent();
            }
        });
    }

    private void cancelAttendEvent() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadEvents: " + sessionManager.getAccessToken());
        callbackAttendEvent = api.attendEvent(sessionManager.getAccessToken(), eventParams);
        callbackAttendEvent.enqueue(new Callback<CallbackStatus>() {
            @Override
            public void onResponse(Call<CallbackStatus> call, Response<CallbackStatus> response) {
                Log.d(TAG, "onResponse: " + response);
                responseAttendEvent = response.body();
                if (responseAttendEvent != null) {
                    if (responseAttendEvent.getSuccess()) {
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onResponse: " + responseAttendEvent.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseAttendEvent.getMessage(), Toast.LENGTH_SHORT).show();
                        eventParams.clear();
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

    private void initComponents(View view) {
        context = getContext();
        activity = getActivity();

        rv_events =view.findViewById(R.id.rv_events);
        layoutManager = new LinearLayoutManager(context);
        rv_events.setLayoutManager(layoutManager);

        customLoader = new CustomLoader(activity, false);
        sessionManager = new SessionManager(context);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh_layout);
        eventsAdapter = new EventsAdapter(context);
        eventsAdapter.addAll(eventsList);

        tv_no=view.findViewById(R.id.tv_no);
    }

    @Override
    public void onRefresh() {
        loadEvents();
    }
}
