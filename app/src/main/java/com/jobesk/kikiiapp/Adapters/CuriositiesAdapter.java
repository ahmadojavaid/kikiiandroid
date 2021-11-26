package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jobesk.kikiiapp.Model.CuriosityChipModel;

import com.jobesk.kikiiapp.R;
import com.plumillonforge.android.chipview.ChipViewAdapter;

public class CuriositiesAdapter extends ChipViewAdapter {

    private String isChecked;
    private Context context;

    public CuriositiesAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public int getLayoutRes(int position) {
        CuriosityChipModel tag = (CuriosityChipModel) getChip(position);
        return R.layout.layout_filters_edit_profile;
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
        CuriosityChipModel tag = (CuriosityChipModel) getChip(position);
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
            return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
            return R.drawable.border_one_corner_round_red_fill;
    }

    @Override
    public void onLayout(View view, int position) {
        CuriosityChipModel tag = (CuriosityChipModel) getChip(position);
        TextView tv_status = view.findViewById(R.id.tv_status);
        tv_status.setText(tag.getText());
    }
}
