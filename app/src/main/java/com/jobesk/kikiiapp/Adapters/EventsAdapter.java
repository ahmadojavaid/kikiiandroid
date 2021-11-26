package com.jobesk.kikiiapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobesk.kikiiapp.Activities.EventDetailActivity;
import com.jobesk.kikiiapp.Model.Attendant;
import com.jobesk.kikiiapp.Model.Event;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.ItemDecorator;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<Event> data;
    private Context context;
    private boolean isLoadingAdded = false;
    private IClicks iClicks;

    public EventsAdapter(Context context) {
        this.context = context;
    }


    public interface IClicks {
        void attendEventClick(View view, Event event, int position);

        void cancelEventClick(View view, Event event, int position);
    }

    public void setOnClickListeners(IClicks iClicks) {
        this.iClicks = iClicks;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_events, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, final int position) {
        final Event event = data.get(position);

        holder.tv_event_title.setText(event.getName());
        holder.tv_time.setText(event.getDatetime());
        Glide
                .with(context)
                .load(event.getCoverPic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder_image)
                .into(holder.img_event);

        List<Attendant> attendantList = event.getAttendants();
        if (attendantList.size() > 8) {
            attendantList.add(7, new Attendant(-1));
        }
        EventAttendantsAdapter eventAttendantsAdapter = new EventAttendantsAdapter(attendantList, context);
        holder.rv_attendants.setAdapter(eventAttendantsAdapter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);

                intent.putExtra("eventID", String.valueOf(event.getId()));

                context.startActivity(intent);
            }
        });

        holder.img_attend_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {
                    iClicks.attendEventClick(v, event, position);
                }
            }
        });
        holder.img_cancel_attend_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClicks != null) {
                    iClicks.cancelEventClick(v, event, position);
                }
            }
        });
    }

    public void add(Event mc) {
        data.add(mc);
        if (data.size() > 1)
            notifyItemInserted(data.size() - 1);
        notifyDataSetChanged();
    }

    public void addAll(List<Event> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(Event city) {
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
        add(new Event());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        Event item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Event getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tv_event_title, tv_time;
        SelectableRoundedImageView img_event;
        RecyclerView rv_attendants;
        ImageView img_attend_event, img_cancel_attend_event;

        public EventViewHolder(View itemView) {
            super(itemView);
            img_event = itemView.findViewById(R.id.img_event);
            img_attend_event = itemView.findViewById(R.id.img_attend_event);
            img_cancel_attend_event = itemView.findViewById(R.id.img_cancel_attend_event);

            tv_time = itemView.findViewById(R.id.tv_time);
            tv_event_title = itemView.findViewById(R.id.tv_event_title);

            rv_attendants = itemView.findViewById(R.id.rv_attendents);
            rv_attendants.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            rv_attendants.addItemDecoration(new ItemDecorator(-20));
        }
    }
}
