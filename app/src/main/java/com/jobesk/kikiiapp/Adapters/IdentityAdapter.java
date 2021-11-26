package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.jobesk.kikiiapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IdentityAdapter extends RecyclerView.Adapter<IdentityAdapter.TravelBuddyViewHolder> {
    private List<String> data;
    private Context context;
    private String selected;
    private int selectedPosition = -1;
    private IClicks iClicks;

    public interface IClicks {
        void onClickListener(View view, String s);
    }

    public void setOnClickListener(IClicks iClicks) {
        this.iClicks = iClicks;
    }

    public IdentityAdapter(List<String> data, Context context, String selected) {
        this.data = data;
        this.context = context;
        this.selected = selected;
        Log.d("TAG", "IdentityAdapter: ");
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_identity, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, final int position) {
        final String name = data.get(position);
        holder.tv_identity.setText(name);

        if (name.equalsIgnoreCase(selected)) {
            holder.img_tick.setVisibility(View.VISIBLE);
        } else {
            holder.img_tick.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {

                    selected = name;
                    selectedPosition = position;
//                    notifyItemChanged(position);
                    notifyDataSetChanged();
                    iClicks.onClickListener(v, name);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_identity, tv_body, tv_date;
        CircleImageView img_user;
        LinearLayout ll_notify;
        ImageView img_tick;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            tv_identity = itemView.findViewById(R.id.tv_identity);
            img_tick = itemView.findViewById(R.id.img_tick);
        }
    }
}
