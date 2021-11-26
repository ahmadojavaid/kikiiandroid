package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Model.Attendant;
import com.jobesk.kikiiapp.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAttendantsDetailAdapter extends RecyclerView.Adapter<EventAttendantsDetailAdapter.EventAttendantViewHolder> {
    private List<Attendant> data;
    Context context;

    public EventAttendantsDetailAdapter(List<Attendant> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public EventAttendantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_image, parent, false);
        return new EventAttendantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventAttendantViewHolder holder, int position) {
        Attendant attendant= data.get(position);
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


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class EventAttendantViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_user;

        public EventAttendantViewHolder(View itemView) {
            super(itemView);
            img_user=itemView.findViewById(R.id.img_user);
        }
    }
}
