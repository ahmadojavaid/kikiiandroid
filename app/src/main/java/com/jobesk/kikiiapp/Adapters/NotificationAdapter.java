package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobesk.kikiiapp.Activities.MyProfileActivity;
import com.jobesk.kikiiapp.Activities.PostDetailActivity;
import com.jobesk.kikiiapp.Model.notificationModel.Notification;
import com.jobesk.kikiiapp.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.TravelBuddyViewHolder> {
    private List<Notification> data;
    Context context;

    public NotificationAdapter(List<Notification> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        Notification model = data.get(position);

        String image = model.getProfilePic();
        Picasso.get().load(image).into(holder.img_user);


//        holder.tv_time_ago.setText(model.getda().toString());


        String type = model.getType();
        if (type.contains("match")) {
            String name = model.getBody();
            holder.textview.setText(name);
        }

        if (type.contains("post_comment")) {
            String name = model.getBody();
            holder.textview.setText(name);
        }


        if (type.contains("post_like")) {
            String name = model.getBody();
            holder.textview.setText(name);
        }

        if (type.contains("comment_reply")) {
            String name = model.getBody();
            holder.textview.setText(name);
        }

        if (type.contains("missed_call")) {
            String name = model.getBody();
            holder.textview.setText(name);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type.equalsIgnoreCase("post_comment") ||
                        type.equalsIgnoreCase("post_like") ||
                        type.equalsIgnoreCase("comment_reply")
                ) {

                    Intent intent = new Intent(context, PostDetailActivity.class);

                    intent.putExtra("postID", String.valueOf(model.getPostId()));
                    context.startActivity(intent);
                }


                if (type.equalsIgnoreCase("match")) {

                    Intent intent = new Intent("go_to_match");
//                    intent.putExtra("key", "My Data");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }


            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_user;
        TextView textview, tv_time_ago;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.textview);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            img_user = itemView.findViewById(R.id.img_user);


        }
    }
}
