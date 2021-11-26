package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Model.Message;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.SessionManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> data;
    private Context context;
    private SessionManager sessionManager;
    private boolean isLoadingAdded = false;

    public ChattingAdapter(Context context) {
        this.data = data;
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    public void add(Message mc) {
        data.add(mc);
        if (data.size() > 1){
            notifyItemInserted(0);
        }
    }

    public void addAll(List<Message> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(Message
                               city) {
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
        add(new Message
                ());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        Message
                item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Message
    getItem(int position) {
        return data.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getSenderId().getId().toString().equalsIgnoreCase(sessionManager.getUserID()))
            return 0;
        else return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                holder = new SenderViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.item_received_message, parent, false);
                holder = new ReceiverViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = data.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.tv_message.setText(message.getBody());
                senderViewHolder.tv_name.setText(message.getSenderId().getName());
                Glide
                        .with(context)
                        .load(message.getSenderId().getProfilePic())
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(senderViewHolder.img_user);
                break;
            case 1:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.tv_message.setText(message.getBody());
                receiverViewHolder.tv_name.setText(message.getSenderId().getName());
                Glide
                        .with(context)
                        .load(message.getSenderId().getProfilePic())
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(receiverViewHolder.img_user);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        LinearLayout ll_detail;

        public SenderViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        LinearLayout ll_detail;

        public ReceiverViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }
}
