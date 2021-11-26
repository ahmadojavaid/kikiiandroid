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
import com.jobesk.kikiiapp.Model.PostComment;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowReplyOfCommentsBottomSheet;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.PostCommentsViewHolder> {
    private List<PostComment> data;
    private Context context;
    private Activity activity;
    private boolean isLoadingAdded;
    private SessionManager sessionManager;
    private IClicks iClicks;


    public interface IClicks {
        void onMenuClick(View view, PostComment data, int position);
    }

    public void addList(List<PostComment> comments) {
        if (comments.size() > 0) {
            for (int i = 0; i < comments.size(); i++) {
                data.add(comments.get(i));
            }
        }
        notifyDataSetChanged();
    }

//    public void setOnClickListeners(IClicks iClicks) {
//        this.iClicks = iClicks;
//    }

    public CommentsAdapter(Activity activity) {
//        this.data = data;
        data = new ArrayList<>();
        this.context = activity;
        this.activity = activity;
        this.iClicks = (IClicks) activity;
        sessionManager = new SessionManager(context);

    }

    @Override
    public PostCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new PostCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostCommentsViewHolder holder, final int position) {
        final PostComment postComment = data.get(position);


        String value = postComment.getCommenter().getName();

        if (value != null) {

            holder.tv_name.setText(postComment.getCommenter().getName().split(" ")[0] + ".");
        }
        if (postComment.getBody() != null) {
            holder.tv_comment.setText(postComment.getBody());
        }


        holder.tv_date.setText(postComment.getCreatedAt());

        Glide
                .with(context)
                .load(postComment.getCommenter().getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(holder.img_user);

        holder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowReplyOfCommentsBottomSheet.showDialog(activity, v, postComment);
            }
        });
        holder.img_comment_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {
                    iClicks.onMenuClick(v, postComment, position);
                }
            }
        });
    }

    public void add(PostComment mc) {
        data.add(mc);
        if (data.size() > 0) {
            notifyItemInserted(0);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<PostComment> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(PostComment city) {
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
        add(new PostComment());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        PostComment item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PostComment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PostCommentsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_comment, tv_date, tv_reply;
        CircleImageView img_user;
        ImageView img_comment_options;

        public PostCommentsViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_reply = itemView.findViewById(R.id.tv_reply);

            img_user = itemView.findViewById(R.id.img_user);
            img_comment_options = itemView.findViewById(R.id.img_comment_options);
        }
    }
}
