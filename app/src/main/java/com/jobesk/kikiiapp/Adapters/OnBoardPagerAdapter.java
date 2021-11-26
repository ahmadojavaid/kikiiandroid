package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.jobesk.kikiiapp.Model.introImages.Image;
import com.jobesk.kikiiapp.R;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OnBoardPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    List<Image> listImages;

    public OnBoardPagerAdapter(Context context, List<Image> listImages) {
        this.context = context;
        this.listImages = listImages;
    }


    @Override
    public int getCount() {
        return listImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sliding_layout, container, false);

        SelectableRoundedImageView img_view_pager = view.findViewById(R.id.img_view_pager);
        TextView tv_heading_pager = view.findViewById(R.id.tv_heading_pager);
        TextView tv_description_pager = view.findViewById(R.id.tv_description_pager);
        String imageLink = listImages.get(position).getPath();

        Picasso.get().load(imageLink).into(img_view_pager);

        tv_heading_pager.setText(listImages.get(position).getTitle());
        tv_description_pager.setText(listImages.get(position).getDescription());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
