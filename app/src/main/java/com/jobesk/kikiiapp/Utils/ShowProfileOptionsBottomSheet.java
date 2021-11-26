package com.jobesk.kikiiapp.Utils;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jobesk.kikiiapp.R;

public class ShowProfileOptionsBottomSheet {


    public static void showDialog(final Activity activity, View view, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                activity, R.style.AppBottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(activity)
                .inflate(R.layout.bottom_sheet_add_image,
                        (RelativeLayout) view.findViewById(R.id.bottomSheetLayout), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bottomSheetView.getBackground().setAlpha(0);
        } else {
            bottomSheetView.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent));
        }
        TextView tv_send_message = bottomSheetView.findViewById(R.id.tv_send_message);
        TextView tv_view_profile = bottomSheetView.findViewById(R.id.tv_view_profile);

        tv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage.getPermissions(activity,1,type);
                bottomSheetDialog.dismiss();
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        tv_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage.getPermissions(activity,2,type);
                bottomSheetDialog.dismiss();
                //activity.startActivity(new Intent(activity, AddMoreProfileImagesActivity.class));
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}
