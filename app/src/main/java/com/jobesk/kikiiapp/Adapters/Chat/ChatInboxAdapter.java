/*
package com.example.kikkiapp.Adapters.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatInboxAdapter extends RecyclerView.Adapter<ChatInboxAdapter.TravelBuddyViewHolder> {
    private List<InboxItem> data;
    Context context;
    private boolean isLoadingAdded;
    private UserService userService;
    private User user = null;

    public ChatInboxAdapter(List<InboxItem> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void add(InboxItem mc) {
        data.add(0, mc);
        if (data.size() > 1)
            notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void addAll(List<InboxItem> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(InboxItem
                               city) {
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
        add(new InboxItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        InboxItem item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public InboxItem
    getItem(int position) {
        return data.get(position);
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_inbox, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        InboxItem inboxItem = data.get(position);

        holder.tv_name.setText(inboxItem.getName());

        if (inboxItem.getProfilePic().equalsIgnoreCase("Group")) {
            GroupLastMessageService groupLastMessageService = new GroupLastMessageService(inboxItem.getUserId());
            groupLastMessageService.setOnChangedListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(EventType type, int index, int oldIndex) {
                }

                @Override
                public void onDataChanged() {
                    if (groupLastMessageService.getCount() > 0) {
                        holder.tv_date.setText(TimeUtils.getTime((long) groupLastMessageService.snapshotForKey("time").getValue()));
                        String type = groupLastMessageService.snapshotForKey("type").getValue().toString();
                        if (type.equalsIgnoreCase(Constants.TYPE_TEXT))
                            holder.tv_message.setText(groupLastMessageService.snapshotForKey("message").getValue().toString());
                        else if (type.equalsIgnoreCase(Constants.TYPE_AUDIO)) {
                            holder.tv_message.setText("Audio");
                        } else if (type.equalsIgnoreCase(Constants.IMAGE)) {
                            holder.tv_message.setText("Image");
                        } else {
                            holder.tv_message.setText("Video");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        } else {
            LastMessageService lastMessageService = new LastMessageService(AppState.currentFireUser.getUid(), inboxItem.getUserId());
            lastMessageService.setOnChangedListener(new ChangeEventListener() {
                @Override
                public void onChildChanged(EventType type, int index, int oldIndex) {
                }

                @Override
                public void onDataChanged() {
                    if (lastMessageService.getCount() > 0) {
                        holder.tv_date.setText(TimeUtils.getTime((long) lastMessageService.snapshotForKey("time").getValue()));
                        String type = lastMessageService.snapshotForKey("type").getValue().toString();
                        if (type.equalsIgnoreCase(Constants.TYPE_TEXT))
                            holder.tv_message.setText(lastMessageService.snapshotForKey("message").getValue().toString());
                        else if (type.equalsIgnoreCase(Constants.TYPE_AUDIO)) {
                            holder.tv_message.setText("Audio");
                        } else if (type.equalsIgnoreCase(Constants.IMAGE)) {
                            holder.tv_message.setText("Image");
                        } else {
                            holder.tv_message.setText("Video");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }


        holder.tv_counter.setText(inboxItem.getCount());
        if (inboxItem.getProfilePic().equalsIgnoreCase("Group")) {
            holder.img_user.setImageResource(R.drawable.ic_dummy_group);
        } else {
            Glide
                    .with(context)
                    .load(inboxItem.getProfilePic())
                    .centerCrop()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .placeholder(R.drawable.ic_dummy_user)
                    .into(holder.img_user);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inboxItem.getProfilePic().equalsIgnoreCase("Group")) {
                    Intent intent = new Intent(context, GroupMessagingActivity.class);
                    intent.putExtra(Constants.ID, inboxItem.getUserId());
                    intent.putExtra(Constants.START_NAME, inboxItem.getName());
                    context.startActivity(intent);
                } else {
                    userService = new UserService();
                    userService.setOnChangedListener(new ChangeEventListener() {
                        @Override
                        public void onChildChanged(EventType type, int index, int oldIndex) {

                        }

                        @Override
                        public void onDataChanged() {
                            user = userService.getUserById(inboxItem.getUserId());
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                    Intent intent = new Intent(context, SingleMessagingActivity.class);
                    intent.putExtra(Constants.ID, inboxItem.getUserId());
                    intent.putExtra(Constants.START_NAME, inboxItem.getName());
                    intent.putExtra(Constants.IMAGE, inboxItem.getProfilePic());
                    intent.putExtra(Constants.CREATE_RIDE_OBJ, user);
                    context.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_message, tv_date, tv_counter;
        CircleImageView img_user;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_counter = itemView.findViewById(R.id.tv_counter);

            img_user = itemView.findViewById(R.id.img_user);
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
*/
