package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jobesk.kikiiapp.Model.KikiiPost;
import com.jobesk.kikiiapp.R;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KikiiPostsAdapter extends RecyclerView.Adapter<KikiiPostsAdapter.KikiiPostViewHolder> {
    private List<KikiiPost> data;
    Context context;
    private IClicks iClicks;
    private boolean isLoadingAdded;

    public interface IClicks {
        void onLikeDislikeClick(View view, KikiiPost data, int position, TextView tv_likes);

        void onCommentClick(View view, KikiiPost data);
    }

    public void setOnClickListeners(IClicks iClicks) {
        this.iClicks = iClicks;
    }

    public KikiiPostsAdapter(Context context) {
        this.data = data;
        data = new ArrayList<>();
        this.context = context;
    }

    public void add(KikiiPost mc) {
        data.add(mc);
        if (data.size() > 1)
            notifyItemInserted(data.size() - 1);
        notifyDataSetChanged();
    }

    public void addList(List<KikiiPost> mc) {
        if (mc.size() > 0) {
            for (int i = 0; i < mc.size(); i++) {
                data.add(mc.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public void addAll(List<KikiiPost> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(KikiiPost city) {
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
        add(new KikiiPost());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        KikiiPost item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public KikiiPost getItem(int position) {
        return data.get(position);
    }

    @Override
    public KikiiPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_kikii_posts, parent, false);
        return new KikiiPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final KikiiPostViewHolder holder, final int position) {
        final KikiiPost kikiiPost = data.get(position);
        holder.tv_description.setText(kikiiPost.getBody());
        holder.tv_likes.setText(String.valueOf(kikiiPost.getLikes_count()));
        holder.tv_comments.setText(String.valueOf(kikiiPost.getComments_count()));

        if (kikiiPost.getIsLiked().toString().equalsIgnoreCase("0"))
            holder.tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_empty_heart, 0, 0, 0);
        else
            holder.tv_likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fill_heart, 0, 0, 0);


        /***CLICKS****/
        holder.tv_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {
                    iClicks.onLikeDislikeClick(v, kikiiPost, position, holder.tv_likes);
                }
            }
        });
        holder.tv_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {
                    iClicks.onCommentClick(v, kikiiPost);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class KikiiPostViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_likes, tv_comments, tv_description, tv_name, tv_time_ago;
        private CircleImageView img_user;

        public KikiiPostViewHolder(View itemView) {
            super(itemView);
            tv_likes = itemView.findViewById(R.id.tv_likes);
            tv_comments = itemView.findViewById(R.id.tv_comments);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);

        }
    }
}
