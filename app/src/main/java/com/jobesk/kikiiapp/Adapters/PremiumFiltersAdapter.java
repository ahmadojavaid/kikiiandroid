package com.jobesk.kikiiapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.jobesk.kikiiapp.Model.filters.FilterModelShow;
import com.jobesk.kikiiapp.R;

import java.util.List;

public class PremiumFiltersAdapter extends RecyclerView.Adapter<PremiumFiltersAdapter.TravelBuddyViewHolder> {
    private List<FilterModelShow> data;

    private Activity activity;

    public PremiumFiltersAdapter(List<FilterModelShow> data, Activity activity) {
        this.data = data;
        this.activity = activity;
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_premium_filters, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        FilterModelShow modelShow = data.get(position);
        String title = modelShow.getTitle();
        holder.tv_status.setText(title);


        if (modelShow.isSelected() == true) {
            holder.layout.setBackground(activity.getResources().getDrawable(R.drawable.border_one_corner_round_red_fill));
        } else {
            holder.layout.setBackground(activity.getResources().getDrawable(R.drawable.filter_bg_grey));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> listValues = modelShow.getValue();

                MultiListAlert(listValues, title, position, modelShow);

//                if (modelShow.isSelected() == true) {
////                 make it grey
//                    holder.layout.setBackground(activity.getResources().getDrawable(R.drawable.filter_bg_grey));
//                    modelShow.setSelected(false);
//                    notifyItemChanged(position);
//
//                } else {
////                 make it red
//                    holder.layout.setBackground(activity.getResources().getDrawable(R.drawable.border_one_corner_round_red_fill));
//                    modelShow.setSelected(true);
//                    notifyItemChanged(position);
//
//                }
            }
        });
    }

    private void MultiListAlert(List<String> listValues, String title, int position, FilterModelShow modelShow) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
//        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select " + title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item);
        arrayAdapter.addAll(listValues);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


              // Toast.makeText(activity, "" + which, Toast.LENGTH_SHORT).show();
                String value = listValues.get(which);

                modelShow.setSelected(true);
                modelShow.setTitle(value);
                notifyItemChanged(position);

            }
        });
        builderSingle.show();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_status;
        RelativeLayout layout;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            tv_status = itemView.findViewById(R.id.tv_status);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
