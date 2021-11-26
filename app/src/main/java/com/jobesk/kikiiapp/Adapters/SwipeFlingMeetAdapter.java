package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.jobesk.kikiiapp.R;

import java.util.ArrayList;

public class SwipeFlingMeetAdapter extends ArrayAdapter<String> {
        Context context;
        public SwipeFlingMeetAdapter(Context context, ArrayList<String> users) {
            super(context, 0, users);
            this.context=context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meet_swipe, parent, false);
            }
            ImageView img_open_details,img_close_details;
            final LinearLayout ll_normal_view;
            final RelativeLayout rl_detail_view;


            img_open_details=convertView.findViewById(R.id.img_open_details);
            img_close_details=convertView.findViewById(R.id.img_close_details);

            ll_normal_view=convertView.findViewById(R.id.ll_normal_view);
            rl_detail_view=convertView.findViewById(R.id.rl_detail_view);

            //rl_detail_view.setVisibility(View.GONE);

            /*// Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
            // Populate the data into the template view using the data object
            tvName.setText(user.name);
            tvHome.setText(user.hometown);*/

            img_close_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                    rl_detail_view.setVisibility(View.GONE);
                    ll_normal_view.setVisibility(View.VISIBLE);
                }
            });

            img_open_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                    rl_detail_view.setVisibility(View.VISIBLE);
                    ll_normal_view.setVisibility(View.GONE);
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }
