package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Model.Attendant;
import com.jobesk.kikiiapp.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAttendantsAdapter extends RecyclerView.Adapter<EventAttendantsAdapter.EventAttendantsViewHolder> {
    private List<Attendant> data;
    Context context;

    public EventAttendantsAdapter(List<Attendant> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public EventAttendantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_event_attendants, parent, false);
        return new EventAttendantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventAttendantsViewHolder holder, int position) {
        Attendant attendant = data.get(position);
        if (attendant.getUserId() == -1) {
            holder.rl_other_likes.setVisibility(View.VISIBLE);
            holder.tv_total_attendants.setVisibility(View.VISIBLE);
            holder.tv_total_attendants.setText("+" + ((data.size()-8)));
        } else {
            Glide
                    .with(context)
                    .load(attendant.getProfilePic())
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_dummy)
                    .into(holder.img_user);
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() > 8) {
            return 8;
        } else {
            return data.size();
        }
    }

    public class EventAttendantsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_total_attendants;
        CircleImageView img_user;
        RelativeLayout rl_other_likes;

        public EventAttendantsViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_user);
            tv_total_attendants = itemView.findViewById(R.id.tv_total_attendants);
            rl_other_likes = itemView.findViewById(R.id.rl_other_likes);
        }
    }
}
