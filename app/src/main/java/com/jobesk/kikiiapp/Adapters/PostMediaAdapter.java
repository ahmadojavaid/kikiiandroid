package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class PostMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> data;
    Context context;
    private boolean isLoadingAdded;
    private IClicks iClicks;
    private String type;
    private boolean isUpdating = false;

    public interface IClicks {
        void onCancelClick(View view, String path, int position);
    }

    public void setOnClickListeners(IClicks iClicks) {
        this.iClicks = iClicks;
    }

    public PostMediaAdapter(List<String> data, Context context, String type, boolean isUpdating) {
        this.data = data;
        this.context = context;
        this.type = type;
        this.isUpdating = isUpdating;
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equalsIgnoreCase("image")) return 1;
        else return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_post_video_media, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.item_post_image_media, parent, false);
                holder = new ImageViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String s = data.get(position);

        Log.d("imagesFilehere", "onBindViewHolder: "+s);
        switch (holder.getItemViewType()) {
            case 0:
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                videoViewHolder.img_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iClicks != null) {
                            iClicks.onCancelClick(v, s, position);
                        }
                    }
                });
                break;
            case 1:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                if (isUpdating) {
                    Glide.with(context)
                            .load(s)
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(imageViewHolder.img_selected);
                } else {
//                    Picasso.get().load(s).fit().centerCrop().into(imageViewHolder.img_selected);

                    imageViewHolder.img_selected.setImageURI(Uri.parse(s));
                }
                imageViewHolder.img_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (iClicks != null) {
                            iClicks.onCancelClick(v, s, position);
                        }
                    }
                });
                break;
        }
    }


    public void add(String mc) {
        data.add(mc);
        if (data.size() > 1)
            notifyItemInserted(data.size() - 1);
        notifyDataSetChanged();
    }

    public void addAll(List<String> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(String city) {
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
        add(new String());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        String item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView img_selected, img_cancel;

        public ImageViewHolder(View itemView) {
            super(itemView);
            img_selected = itemView.findViewById(R.id.img_selected);
            img_cancel = itemView.findViewById(R.id.img_cancel);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView img_selected, img_cancel;

        public VideoViewHolder(View itemView) {
            super(itemView);
            img_selected = itemView.findViewById(R.id.img_selected);
            img_cancel = itemView.findViewById(R.id.img_cancel);
        }
    }
}
