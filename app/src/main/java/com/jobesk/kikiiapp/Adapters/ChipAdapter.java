package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jobesk.kikiiapp.Model.ChipModel;

import com.jobesk.kikiiapp.R;
import com.plumillonforge.android.chipview.ChipViewAdapter;

public class ChipAdapter extends ChipViewAdapter {

    private String isChecked;
    private Context context;

    public ChipAdapter(Context context, String isChecked) {
        super(context);
        this.context=context;
        this.isChecked = isChecked;
    }

    @Override
    public int getLayoutRes(int position) {
        ChipModel tag = (ChipModel) getChip(position);
        return R.layout.item_status;
        /*switch (tag.getType()) {
            default:
            case 2:
            case 4:
                return 0;

            case 1:
            case 5:
                return R.layout.item_status;

            case 3:
                return R.layout.item_status;
        }*/
    }

    @Override
    public int getBackgroundColor(int position) {
        ChipModel tag = (ChipModel) getChip(position);
        return 0;
       /* switch (tag.getType()) {
            default:
                return 0;

            case 1:
            case 4:
                return getColor(R.color.colorPrimaryDark);

            case 2:
            case 5:
                return getColor(R.color.colorPrimaryDark);

            case 3:
                return getColor(R.color.colorPrimaryDark);
        }*/
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        ChipModel tag = (ChipModel) getChip(position);
        if (isChecked.equalsIgnoreCase(tag.getText())){
            return R.drawable.border_one_corner_round_red_fill;
        }
        else{
            return R.drawable.border_one_corner_round_red;
        }
    }

    @Override
    public int getBackgroundRes(int position) {
        ChipModel tag = (ChipModel) getChip(position);
        if (isChecked.equalsIgnoreCase(tag.getText())){
            return R.drawable.border_one_corner_round_red_fill;
        }
        else{
            return R.drawable.border_one_corner_round_red;
        }
    }

    @Override
    public void onLayout(View view, int position) {
        ChipModel tag = (ChipModel) getChip(position);
        TextView tv_status = view.findViewById(R.id.tv_status);
        tv_status.setText(tag.getText());

        if (isChecked.equalsIgnoreCase(tag.getText())){
            tv_status.setTextColor(getColor(R.color.white));
        }
        else{
            tv_status.setTextColor(getColor(R.color.grey));
        }
    }
}
