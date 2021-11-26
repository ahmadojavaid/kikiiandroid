package com.jobesk.kikiiapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobesk.kikiiapp.Model.BlockedUserModel.BlockedUser;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.recyclerviewCallbacks.BlocledUserInterfaceCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.TravelBuddyViewHolder> {
    private List<BlockedUser> data;
    Activity activity;

    BlocledUserInterfaceCallback listner;

    public BlockedUserAdapter(Activity activity, List<BlockedUser> data) {

        this.data = data;
        this.activity = activity;
        this.listner = (BlocledUserInterfaceCallback) activity;


    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_blocked_users, parent, false);


        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, final int position) {
        BlockedUser model = data.get(position);


        Picasso.get().load(model.getProfilePic()).into(holder.img_user);

        holder.tv_name.setText(model.getName());

        holder.img_my_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = model.getId();

                data.remove(position);
                notifyDataSetChanged();
                listner.onClick(position, id);


            }
        });


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {


        ImageView img_user;
        private TextView tv_name;
        private ImageView img_my_friend;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_my_friend = itemView.findViewById(R.id.img_my_friend);

        }
    }
}
