package com.jobesk.kikiiapp.Adapters.Chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jobesk.kikiiapp.Firebase.AppState;
import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.jobesk.kikiiapp.Firebase.Model.Message;
import com.jobesk.kikiiapp.Netwrok.Constants;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.AudioPlayer;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.TimeUtils;
import com.jobesk.kikiiapp.Utils.VideoViewActivity;
import com.joooonho.SelectableRoundedImageView;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> data;
    private Context context;
    private Activity activity;
    private SessionManager sessionManager;
    private boolean isLoadingAdded = false;
    private FirebaseUserModel receiver = null;
    private AudioPlayer audioPlayer;
    private IClick iClick;

    public interface IClick{
        void onLongPressListener(View v,Message message);
        void onClickListener(View v,Message message);
    }
    public void setOnLongPressListener(IClick iClick){
        this.iClick=iClick;
    }

    public MessagingAdapter(List<Message> data, Context context, Activity activity, FirebaseUserModel receiver) {
        this.data = data;
        this.context = context;
        this.activity = activity;
        this.receiver = receiver;
        sessionManager = new SessionManager(context);
        audioPlayer = new AudioPlayer(false);
    }

    public void add(Message mc) {
        data.add(0, mc);
        if (data.size() > 1)
            notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void addAll(List<Message> mcList) {
        data = mcList;
        notifyDataSetChanged();
    }

    public void remove(Message
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
        add(new Message
                ());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = data.size() - 1;
        Message
                item = getItem(position);
        if (item != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Message
    getItem(int position) {
        return data.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getMessageBy().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
            if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_TEXT))
                return 0;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_AUDIO))
                return 2;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_IMAGE))
                return 4;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_VIDEO))
                return 6;
            else return -1;
        } else {
            if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_TEXT))
                return 1;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_AUDIO))
                return 3;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_IMAGE))
                return 5;
            else if (data.get(position).getType().equalsIgnoreCase(Constants.TYPE_VIDEO))
                return 7;
            else
                return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                holder = new SenderTextViewHolder(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.item_sent_audio, parent, false);
                holder = new SenderAudioViewHolder(view);
                break;
            case 4:
                view = inflater.inflate(R.layout.item_sent_image, parent, false);
                holder = new SenderImageViewHolder(view);
                break;
            case 6:
                view = inflater.inflate(R.layout.item_sent_video, parent, false);
                holder = new SenderVideoViewHolder(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.item_received_message, parent, false);
                holder = new ReceiverTextViewHolder(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.item_received_audio, parent, false);
                holder = new ReceiverAudioViewHolder(view);
                break;
            case 5:
                view = inflater.inflate(R.layout.item_received_image, parent, false);
                holder = new ReceiverImageViewHolder(view);
                break;
            case 7:
                view = inflater.inflate(R.layout.item_received_video, parent, false);
                holder = new ReceiverVideoViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = data.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(iClick!=null){
                    iClick.onLongPressListener(v,message);
                }
                return true;
            }
        });
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(iClick!=null){
                   iClick.onClickListener(v,message);
               }
           }
       });
        switch (holder.getItemViewType()) {
            case 0:
                SenderTextViewHolder senderTextViewHolder = (SenderTextViewHolder) holder;
                senderTextViewHolder.tv_message.setText(message.getMessage());
                if (sessionManager.getProfileUser().getUpgraded().toString().equalsIgnoreCase("1")) {
                    if (data.get(position).getSeen().equalsIgnoreCase("true")) {
                        senderTextViewHolder.img_seen.setVisibility(View.VISIBLE);
                        senderTextViewHolder.img_single_tick.setVisibility(View.GONE);
                    } else {
                        senderTextViewHolder.img_seen.setVisibility(View.GONE);
                        senderTextViewHolder.img_single_tick.setVisibility(View.VISIBLE);
                    }
                } else {
                    senderTextViewHolder.img_seen.setVisibility(View.GONE);
                    senderTextViewHolder.img_single_tick.setVisibility(View.GONE);
                }

                /****SHOW USER INFO****/
                if (position == 0) {
                    senderTextViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    senderTextViewHolder.tv_name.setText(sessionManager.getUserName());
                    senderTextViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(sessionManager.getProfileUser().getProfilePic())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(senderTextViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                        senderTextViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        senderTextViewHolder.tv_name.setText(sessionManager.getUserName());
                        senderTextViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(sessionManager.getProfileUser().getProfilePic())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(senderTextViewHolder.img_user);
                    } else {
                        senderTextViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/

                break;
            case 2:
                final SenderAudioViewHolder senderAudioViewHolder = (SenderAudioViewHolder) holder;

                senderAudioViewHolder.tv_duration.setText(audioPlayer.getAudioDuration(message.getRecordingTime()));
                if (sessionManager.getProfileUser().getUpgraded().toString().equalsIgnoreCase("1")) {
                    if (data.get(position).getSeen().equalsIgnoreCase("true")) {
                        senderAudioViewHolder.img_seen.setVisibility(View.VISIBLE);
                        senderAudioViewHolder.img_single_tick.setVisibility(View.GONE);
                    } else {
                        senderAudioViewHolder.img_seen.setVisibility(View.GONE);
                        senderAudioViewHolder.img_single_tick.setVisibility(View.VISIBLE);
                    }
                } else {
                    senderAudioViewHolder.img_seen.setVisibility(View.GONE);
                    senderAudioViewHolder.img_single_tick.setVisibility(View.GONE);
                }

                senderAudioViewHolder.img_play_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        senderAudioViewHolder.img_pause_audio.setVisibility(View.VISIBLE);
                        senderAudioViewHolder.img_play_audio.setVisibility(View.GONE);
                        audioPlayer.startPlayer(activity, message.getMessage(), senderAudioViewHolder.seek_audio, senderAudioViewHolder.img_play_audio, senderAudioViewHolder.img_pause_audio, message.getRecordingTime());
                    }
                });
                senderAudioViewHolder.img_pause_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        senderAudioViewHolder.img_pause_audio.setVisibility(View.GONE);
                        senderAudioViewHolder.img_play_audio.setVisibility(View.VISIBLE);
                        audioPlayer.stopPlayer(senderAudioViewHolder.seek_audio, senderAudioViewHolder.img_play_audio, senderAudioViewHolder.img_pause_audio);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    senderAudioViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    senderAudioViewHolder.tv_name.setText(sessionManager.getUserName());
                    senderAudioViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(sessionManager.getProfileUser().getProfilePic())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(senderAudioViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                        senderAudioViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        senderAudioViewHolder.tv_name.setText(sessionManager.getUserName());
                        senderAudioViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(sessionManager.getProfileUser().getProfilePic())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(senderAudioViewHolder.img_user);
                    } else {
                        senderAudioViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
            case 4:
                SenderImageViewHolder senderImageViewHolder = (SenderImageViewHolder) holder;
                if (sessionManager.getProfileUser().getUpgraded().toString().equalsIgnoreCase("1")) {
                    if (data.get(position).getSeen().equalsIgnoreCase("true")) {
                        senderImageViewHolder.img_seen.setVisibility(View.VISIBLE);
                        senderImageViewHolder.img_single_tick.setVisibility(View.GONE);
                    } else {
                        senderImageViewHolder.img_seen.setVisibility(View.GONE);
                        senderImageViewHolder.img_single_tick.setVisibility(View.VISIBLE);
                    }
                } else {
                    senderImageViewHolder.img_seen.setVisibility(View.GONE);
                    senderImageViewHolder.img_single_tick.setVisibility(View.GONE);
                }

                Glide
                        .with(context)
                        .load(message.getMessage())
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_place_holder_image)
                        .into(senderImageViewHolder.img_media_image);
                senderImageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> array = new ArrayList<>();
                        array.add(message.getMessage());
                        showImageViewer(0, array);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    senderImageViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    senderImageViewHolder.tv_name.setText(sessionManager.getUserName());
                    senderImageViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(sessionManager.getProfileUser().getProfilePic())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(senderImageViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                        senderImageViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        senderImageViewHolder.tv_name.setText(sessionManager.getUserName());
                        senderImageViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(sessionManager.getProfileUser().getProfilePic())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(senderImageViewHolder.img_user);
                    } else {
                        senderImageViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
            case 6:
                SenderVideoViewHolder senderVideoViewHolder = (SenderVideoViewHolder) holder;
                if (sessionManager.getProfileUser().getUpgraded().toString().equalsIgnoreCase("1")) {
                    if (data.get(position).getSeen().equalsIgnoreCase("true")) {
                        senderVideoViewHolder.img_seen.setVisibility(View.VISIBLE);
                        senderVideoViewHolder.img_single_tick.setVisibility(View.GONE);
                    } else {
                        senderVideoViewHolder.img_seen.setVisibility(View.GONE);
                        senderVideoViewHolder.img_single_tick.setVisibility(View.VISIBLE);
                    }
                } else {
                    senderVideoViewHolder.img_seen.setVisibility(View.GONE);
                    senderVideoViewHolder.img_single_tick.setVisibility(View.GONE);
                }


                RequestOptions requestOptions = new RequestOptions();
                requestOptions.isMemoryCacheable();
                String videopath = message.getMessage();
                Glide.with(activity).setDefaultRequestOptions(requestOptions).load(videopath).into(senderVideoViewHolder.first_image_view);
                senderVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("videoLink", "onClick: " + message.getMessage());
                        Intent intent = new Intent(activity, VideoViewActivity.class);
                        intent.putExtra("videoLink", message.getMessage());
                        intent.putExtra("from", "firebase");
                        activity.startActivity(intent);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    senderVideoViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    senderVideoViewHolder.tv_name.setText(sessionManager.getUserName());
                    senderVideoViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(sessionManager.getProfileUser().getProfilePic())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(senderVideoViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(AppState.currentBpackCustomer.getUserId())) {
                        senderVideoViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        senderVideoViewHolder.tv_name.setText(sessionManager.getUserName());
                        senderVideoViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(sessionManager.getProfileUser().getProfilePic())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(senderVideoViewHolder.img_user);
                    } else {
                        senderVideoViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
            case 1:
                ReceiverTextViewHolder receiverTextViewHolder = (ReceiverTextViewHolder) holder;
                receiverTextViewHolder.tv_message.setText(message.getMessage());
                /****SHOW USER INFO****/
                if (position == 0) {
                    receiverTextViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    receiverTextViewHolder.tv_name.setText(receiver.getUserName());
                    receiverTextViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(receiver.getImage())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(receiverTextViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(receiver.getUserId())) {
                        receiverTextViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        receiverTextViewHolder.tv_name.setText(receiver.getUserName());
                        receiverTextViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(receiver.getImage())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(receiverTextViewHolder.img_user);
                    } else {
                        receiverTextViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/

                break;
            case 3:
                final ReceiverAudioViewHolder receiverAudioViewHolder = (ReceiverAudioViewHolder) holder;
                receiverAudioViewHolder.tv_duration.setText(audioPlayer.getAudioDuration(message.getRecordingTime()));
                receiverAudioViewHolder.img_play_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiverAudioViewHolder.img_play_audio.setVisibility(View.GONE);
                        receiverAudioViewHolder.img_pause_audio.setVisibility(View.VISIBLE);
                        audioPlayer.startPlayer(activity, message.getMessage(), receiverAudioViewHolder.seek_audio, receiverAudioViewHolder.img_play_audio, receiverAudioViewHolder.img_pause_audio, message.getRecordingTime());
                    }
                });
                receiverAudioViewHolder.img_pause_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiverAudioViewHolder.img_play_audio.setVisibility(View.VISIBLE);
                        receiverAudioViewHolder.img_play_audio.setVisibility(View.GONE);
                        audioPlayer.stopPlayer(receiverAudioViewHolder.seek_audio, receiverAudioViewHolder.img_play_audio, receiverAudioViewHolder.img_pause_audio);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    receiverAudioViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    receiverAudioViewHolder.tv_name.setText(receiver.getUserName());
                    receiverAudioViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(receiver.getImage())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(receiverAudioViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(receiver.getUserId())) {
                        receiverAudioViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        receiverAudioViewHolder.tv_name.setText(receiver.getUserName());
                        receiverAudioViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(receiver.getImage())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(receiverAudioViewHolder.img_user);
                    } else {
                        receiverAudioViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
            case 5:
                ReceiverImageViewHolder receiverImageViewHolder = (ReceiverImageViewHolder) holder;
                Glide
                        .with(context)
                        .load(message.getMessage())
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_place_holder_image)
                        .into(receiverImageViewHolder.img_media_image);
                receiverImageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> array = new ArrayList<>();
                        array.add(message.getMessage());
                        showImageViewer(0, array);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    receiverImageViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    receiverImageViewHolder.tv_name.setText(receiver.getUserName());
                    receiverImageViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(receiver.getImage())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(receiverImageViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(receiver.getUserId())) {
                        receiverImageViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        receiverImageViewHolder.tv_name.setText(receiver.getUserName());
                        receiverImageViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(receiver.getImage())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(receiverImageViewHolder.img_user);
                    } else {
                        receiverImageViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
            case 7:
                ReceiverVideoViewHolder receiverVideoViewHolder = (ReceiverVideoViewHolder) holder;
                RequestOptions requestOptions1 = new RequestOptions();
                requestOptions1.isMemoryCacheable();
                String videopath1 = message.getMessage();
                Glide.with(activity).setDefaultRequestOptions(requestOptions1).load(videopath1).into(receiverVideoViewHolder.first_image_view);
                receiverVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("videoLink", "onClick: " + message.getMessage());
                        Intent intent = new Intent(activity, VideoViewActivity.class);
                        intent.putExtra("videoLink", message.getMessage());
                        activity.startActivity(intent);
                    }
                });
                /****SHOW USER INFO****/
                if (position == 0) {
                    receiverVideoViewHolder.ll_detail.setVisibility(View.VISIBLE);
                    receiverVideoViewHolder.tv_name.setText(receiver.getUserName());
                    receiverVideoViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                    Glide
                            .with(context)
                            .load(receiver.getImage())
                            .centerCrop()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_dummy)
                            .into(receiverVideoViewHolder.img_user);
                } else {
                    if (!data.get(position - 1).getMessageBy().equalsIgnoreCase(receiver.getUserId())) {
                        receiverVideoViewHolder.ll_detail.setVisibility(View.VISIBLE);
                        receiverVideoViewHolder.tv_name.setText(receiver.getUserName());
                        receiverVideoViewHolder.tv_time_ago.setText(TimeUtils.getTime(message.getTime()));
                        Glide
                                .with(context)
                                .load(receiver.getImage())
                                .centerCrop()
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .placeholder(R.drawable.ic_user_dummy)
                                .into(receiverVideoViewHolder.img_user);
                    } else {
                        receiverVideoViewHolder.ll_detail.setVisibility(View.GONE);
                    }
                }
                /****SHOW USER INFO****/
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SenderTextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        LinearLayout ll_detail;
        ImageView img_single_tick, img_seen;

        public SenderTextViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);

            img_user = itemView.findViewById(R.id.img_user);
            img_seen = itemView.findViewById(R.id.img_seen);
            img_single_tick = itemView.findViewById(R.id.img_single_tick);

            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class SenderAudioViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        ImageView img_play_audio, img_pause_audio;
        SeekBar seek_audio;
        TextView tv_duration;
        LinearLayout ll_detail;
        ImageView img_single_tick, img_seen;

        public SenderAudioViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);

            img_user = itemView.findViewById(R.id.img_user);
            img_play_audio = itemView.findViewById(R.id.img_play_audio);
            img_pause_audio = itemView.findViewById(R.id.img_pause_audio);
            img_seen = itemView.findViewById(R.id.img_seen);
            img_single_tick = itemView.findViewById(R.id.img_single_tick);

            seek_audio = itemView.findViewById(R.id.seek_audio);

            tv_duration = itemView.findViewById(R.id.tv_duration);

            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class SenderImageViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        SelectableRoundedImageView img_media_image;
        CircleImageView img_user;
        LinearLayout ll_detail;
        ImageView img_single_tick, img_seen;


        public SenderImageViewHolder(View itemView) {
            super(itemView);
            img_media_image = itemView.findViewById(R.id.img_media_image);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);

            img_user = itemView.findViewById(R.id.img_user);
            img_seen = itemView.findViewById(R.id.img_seen);
            img_single_tick = itemView.findViewById(R.id.img_single_tick);

            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class SenderVideoViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        LinearLayout ll_detail;
        ImageView first_image_view;
        RelativeLayout rl_video;
        ImageView img_single_tick, img_seen;

        public SenderVideoViewHolder(View itemView) {
            super(itemView);
            rl_video = itemView.findViewById(R.id.rl_video);
            first_image_view = itemView.findViewById(R.id.first_image_view);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);
            tv_name = itemView.findViewById(R.id.tv_name);

            img_user = itemView.findViewById(R.id.img_user);
            img_seen = itemView.findViewById(R.id.img_seen);
            img_single_tick = itemView.findViewById(R.id.img_single_tick);

            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class ReceiverTextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        CircleImageView img_user;
        LinearLayout ll_detail;

        public ReceiverTextViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);

            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class ReceiverAudioViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        ImageView img_play_audio, img_pause_audio;
        SeekBar seek_audio;
        TextView tv_duration;
        LinearLayout ll_detail;
        CircleImageView img_user;

        public ReceiverAudioViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            img_user = itemView.findViewById(R.id.img_user);

            img_play_audio = itemView.findViewById(R.id.img_play_audio);
            img_pause_audio = itemView.findViewById(R.id.img_pause_audio);

            seek_audio = itemView.findViewById(R.id.seek_audio);

            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);

            tv_name = itemView.findViewById(R.id.tv_name);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class ReceiverImageViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        SelectableRoundedImageView img_media_image;
        LinearLayout ll_detail;
        CircleImageView img_user;

        public ReceiverImageViewHolder(View itemView) {
            super(itemView);
            img_media_image = itemView.findViewById(R.id.img_media_image);

            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);

            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    public class ReceiverVideoViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message, tv_time_ago, tv_name;
        ImageView first_image_view;
        RelativeLayout rl_video;
        LinearLayout ll_detail;
        CircleImageView img_user;

        public ReceiverVideoViewHolder(View itemView) {
            super(itemView);
            rl_video = itemView.findViewById(R.id.rl_video);
            first_image_view = itemView.findViewById(R.id.first_image_view);
            tv_time_ago = itemView.findViewById(R.id.tv_time_ago);

            tv_name = itemView.findViewById(R.id.tv_name);
            img_user = itemView.findViewById(R.id.img_user);
            ll_detail = itemView.findViewById(R.id.ll_detail);
        }
    }

    private void showImageViewer(int pos, ArrayList<String> array) {
        new StfalconImageViewer.Builder<>(context, array, new ImageLoader<String>() {
            @Override
            public void loadImage(ImageView imageView, String path) {
                Glide.with(context).load(path).into(imageView);
            }
        })
                .withStartPosition(pos)
                .show();
    }
}
