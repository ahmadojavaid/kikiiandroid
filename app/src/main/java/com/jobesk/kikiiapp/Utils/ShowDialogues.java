package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jobesk.kikiiapp.Activities.MyProfileActivity;
import com.jobesk.kikiiapp.Activities.SupportActivity;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.google.android.material.snackbar.Snackbar;
import com.jobesk.kikiiapp.R;


import java.util.Calendar;

import retrofit2.Call;

public class ShowDialogues {
    private static final String TAG = "ShowDialogues";
    static PopupWindow mypopupWindow = null;
    static SessionManager sessionManager;
    static CustomLoader customLoader;
    private static Call<CallbackUpdateProfile> callbackStatusCall;
    private static CallbackUpdateProfile responseLatLongUpdate;

    public static void SHOW_DATE_PICKER_DIALOG(Context context, final TextView tv, DialogInterface.OnDismissListener onDismissListener) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialogue_date_picker);
        alertDialog.setOnDismissListener(onDismissListener);
        final DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        Calendar c = Calendar.getInstance();
        datePicker.setMaxDate(c.getTimeInMillis());
        Button btn_ok = alertDialog.findViewById(R.id.btn_ok);
        Button btn_cancel = alertDialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int monthOfYear = datePicker.getMonth() + 1;
                int dayOfMonth = datePicker.getDayOfMonth();
                String dateYouChoose = null;
                if (dayOfMonth < 10 && monthOfYear < 10)
                    dateYouChoose = year + "-" + "0" + (monthOfYear) + "-" + "0" + dayOfMonth;
                else if (dayOfMonth < 10)
                    dateYouChoose = year + "-" + (monthOfYear) + "-" + "0" + dayOfMonth;
                else if (monthOfYear < 10)
                    dateYouChoose = year + "-" + "0" + (monthOfYear) + "-" + dayOfMonth;
                else
                    dateYouChoose = year + "-" + (monthOfYear) + "-" + dayOfMonth;
                tv.setText(dateYouChoose);
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public static void SHOW_NO_INTERNET_DIALOG(Context context) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialogue_no_internet);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.findViewById(R.id.tv_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void SHOW_SERVER_ERROR_DIALOG(Context context) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_server_error);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.findViewById(R.id.tv_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void SHOW_SNACK_BAR(View parentLayout, Activity activity, String text) {
        Snackbar snackbar = Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG);
        View customView = activity.getLayoutInflater().inflate(R.layout.snackbar_internet_connection, null);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        TextView tv_subject = customView.findViewById(R.id.tv_subject);
        tv_subject.setText(text);
        snackbarLayout.addView(customView);
        snackbar.show();
    }

    public static void showPostMenu(final Activity activity, View view, ViewGroup viewGroup) {
        sessionManager = new SessionManager(activity);
        LayoutInflater inflater = (LayoutInflater)
                activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.home_menu, viewGroup, false);

        TextView tv_profile = v.findViewById(R.id.tv_profile);
        TextView tv_incognito = v.findViewById(R.id.tv_incognito);
        TextView tv_rewind = v.findViewById(R.id.tv_rewind);
        TextView tv_support = v.findViewById(R.id.tv_support);
        TextView tv_logout = v.findViewById(R.id.tv_logout);

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        tv_incognito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
               // onIncognitoMode(activity,params);
                //activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        tv_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                //activity.startActivity(new Intent(activity, MyProfileActivity.class));
            }
        });
        tv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                activity.startActivity(new Intent(activity, SupportActivity.class));
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
                sessionManager.logoutUser();
                activity.finish();

            }
        });
        mypopupWindow = new PopupWindow(v, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        if (v.getParent() != null) {
            ((ViewGroup) v.getParent()).removeView(v); // <- fix
        }
        mypopupWindow.showAsDropDown(view, 0, 0);

        mypopupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mypopupWindow.dismiss();
            }
        });
    }

    public boolean isShowingMenu() {
        if (mypopupWindow.isShowing()) return true;
        else return false;
    }
}
