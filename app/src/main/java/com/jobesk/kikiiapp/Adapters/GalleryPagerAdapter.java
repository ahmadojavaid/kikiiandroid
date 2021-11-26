package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.jobesk.kikiiapp.R;


import java.util.ArrayList;


public class GalleryPagerAdapter extends PagerAdapter {
    private Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<Integer> gallery;

    public GalleryPagerAdapter(Context context, ArrayList<Integer> gallery) {
        this.mContext = context;
        this.gallery=gallery;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.item_pager_gallery, container, false);
        ImageView iv_bottom_foster = (ImageView) itemView.findViewById(R.id.iv_bottom_foster);

        Glide
                .with(mContext)
                .load(gallery.get(position))
                .placeholder(R.drawable.ic_place_holder_image)
                .into(iv_bottom_foster);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return gallery.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}