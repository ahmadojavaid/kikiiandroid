package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Model.FellowUser;
import com.jobesk.kikiiapp.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    private List<FellowUser> data;
    Context context;
    private boolean isLoadingAdded;
    private String type;
    private IClicks iClicks;

    public interface IClicks {
        void onFollowUser(View view, FellowUser user);

        void onUnFollowUser(View view, FellowUser user);
    }

    public void setOnClickListener(IClicks iClicks) {
        this.iClicks = iClicks;
    }

    public FriendsAdapter(String type, List<FellowUser> data, Context context) {
        this.data = data;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equalsIgnoreCase("myFriends")) return 0;
        else if (type.equalsIgnoreCase("pendings")) return 1;
        else return 2;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_friends, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, int position) {
        FellowUser user = data.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                holder.img_my_friend.setVisibility(View.VISIBLE);
                holder.img_pending_requests.setVisibility(View.GONE);
                holder.img_cancel_request.setVisibility(View.GONE);
                break;
            case 1:
                holder.img_my_friend.setVisibility(View.GONE);
                holder.img_pending_requests.setVisibility(View.VISIBLE);
                holder.img_cancel_request.setVisibility(View.GONE);
                break;
            case 2:
                holder.img_my_friend.setVisibility(View.GONE);
                holder.img_pending_requests.setVisibility(View.GONE);
                holder.img_cancel_request.setVisibility(View.VISIBLE);
                break;
        }

        holder.tv_name.setText(user.getName());
        Glide.with(context)
                .load(user.getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(holder.img_user);

        holder.img_cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {

                    iClicks.onUnFollowUser(v, user);

                    data.remove(position);
                  notifyDataSetChanged();
                }
            }
        });
        holder.img_pending_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {

                    iClicks.onFollowUser(v, user);

                    data.remove(position);
                    notifyDataSetChanged();


                }
            }
        });

    }

    public void add(FellowUser mc) {
        data.add(mc);
        if (data.size() > 1)
            notifyItemInserted(data.size() - 1);
        notifyDataSetChanged();
    }

    public void addAll(List<FellowUser> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(FellowUser city) {
        int position = data.indexOf(city);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new FellowUser());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        FellowUser item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public FellowUser getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        CircleImageView img_user;
        ImageView img_my_friend, img_pending_requests, img_cancel_request;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            img_my_friend = itemView.findViewById(R.id.img_my_friend);
            img_pending_requests = itemView.findViewById(R.id.img_pending_requests);
            img_cancel_request = itemView.findViewById(R.id.img_cancel_request);
        }
    }
}
