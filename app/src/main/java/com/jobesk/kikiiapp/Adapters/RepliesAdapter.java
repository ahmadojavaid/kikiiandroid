package com.jobesk.kikiiapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Activities.CreatePostActivity;
import com.jobesk.kikiiapp.Model.CommentReply;
import com.jobesk.kikiiapp.R;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.PostCommentsViewHolder> {
    private List<CommentReply> data;
    private Context context;
    private Activity activity;
    private boolean isLoadingAdded;

    public RepliesAdapter(Activity activity) {
//        this.data = data;
        data=new ArrayList<>();
        this.context = activity;
        this.activity = activity;
    }
    @Override
    public PostCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_reply, parent, false);
        return new PostCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostCommentsViewHolder holder, int position) {
        final CommentReply commentReply = data.get(position);

        holder.tv_name.setText(commentReply.getUser().getName());
        holder.tv_reply.setText(commentReply.getBody());
        Glide
                .with(context)
                .load(commentReply.getUser().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(holder.img_user);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CreatePostActivity.class));
            }
        });
    }
    public void add(CommentReply mc) {
        data.add(mc);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List <CommentReply> mcList) {
        for (CommentReply mc: mcList) {
            add(mc);
        }
    }

    public void remove(CommentReply city) {
        int position = data.indexOf(city);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
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
        add(new CommentReply());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        CommentReply item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public CommentReply getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PostCommentsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date,tv_reply;
        CircleImageView img_user;

        public PostCommentsViewHolder(View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_date=itemView.findViewById(R.id.tv_date);
            tv_reply=itemView.findViewById(R.id.tv_reply);

            img_user=itemView.findViewById(R.id.img_user);
        }
    }
}
