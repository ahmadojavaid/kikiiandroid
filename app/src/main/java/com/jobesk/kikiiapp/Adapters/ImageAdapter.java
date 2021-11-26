package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jobesk.kikiiapp.Model.AdsImagesModel.Image;
import com.jobesk.kikiiapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/13/2021.
 * sherazbhutta07@gmail.com
 */
public class ImageAdapter extends PagerAdapter {

    Context context;

    LayoutInflater mLayoutInflater;
    List<Image> arrayListAds;

    public ImageAdapter(Context context, List<Image> arrayListAds) {
        this.context = context;
        this.arrayListAds = arrayListAds;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayListAds.size();
    }

//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((LinearLayout) object);
//        }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.page_viewer_pics, container, false);

        String imagePos = arrayListAds.get(position).getPath();
        ImageView pic_img = itemView.findViewById(R.id.imageView);
        Picasso.get().load(imagePos).fit().centerCrop().into(pic_img);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}