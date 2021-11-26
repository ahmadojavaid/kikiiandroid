package com.jobesk.kikiiapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Model.Match;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.ShowSelectImageBottomSheet;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class YourMatchAdapter extends RecyclerView.Adapter<YourMatchAdapter.TravelBuddyViewHolder> {
    private List<Match> data;
    Context context;
    Activity activity;

    public YourMatchAdapter(List<Match> data, Context context, Activity activity) {
        this.data = data;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_your_matches, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        final Match match = data.get(position);

        String onlineDate = match.getLastOnline();

        if (onlineDate.contains("min. ago")) {

            String number = onlineDate.replaceAll("[^0-9]", "");

            if (Integer.parseInt(number) <= 5) {
                holder.isOnlineImg.setBackground(activity.getResources().getDrawable(R.drawable.ic_online));


            } else {
                holder.isOnlineImg.setBackground(activity.getResources().getDrawable(R.drawable.ic_offline));
            }

        } else {
            holder.isOnlineImg.setBackground(activity.getResources().getDrawable(R.drawable.ic_offline));

        }


        holder.tv_time_ago.setText(match.getLastOnline());

        holder.tv_user_name.setText(match.getName().split(" ")[0] + ".");
        Glide.with(context)
                .load(match.getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(holder.img_user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSelectImageBottomSheet.showDialogProfileOptions(activity, v, match.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name, tv_time_ago;
        SelectableRoundedImageView img_user;
        ImageView isOnlineImg;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_user);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            isOnlineImg = itemView.findViewById(R.id.isOnlineImg);
        }
    }
}
