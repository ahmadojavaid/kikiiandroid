package com.jobesk.kikiiapp.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.internal.$Gson$Preconditions;
import com.jobesk.kikiiapp.Activities.SpecificUserFriendsActivity;
import com.jobesk.kikiiapp.Activities.SpecificUserPosts;
import com.jobesk.kikiiapp.Model.MeetUser;
import com.jobesk.kikiiapp.Netwrok.Constants;

import com.jobesk.kikiiapp.R;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class MeetCardSwipeStackAdapter extends RecyclerView.Adapter<MeetCardSwipeStackAdapter.TravelBuddyViewHolder> {
    private List<MeetUser> data;
    private Context context;
    private IListEnd iListEnd;
    private IClick iClick;

    public interface IListEnd {
        void onListEndListener();
    }

    public interface IClick {
        void onLikeUserClick(MeetUser user,int position);

        void onDislikeUserClick(MeetUser user,int position);

        void onFollowUserClick(MeetUser user,int position);

        void onBlockUserClick(MeetUser user, int position);

        void onReportUserClick(MeetUser user,int position);

        void onShareUserProfile(MeetUser user,int position);
    }

    public void setOnListEndListener(IListEnd iListEnd) {
        this.iListEnd = iListEnd;
    }

    public void setOnClickListener(IClick iClick) {
        this.iClick = iClick;
    }

    public MeetCardSwipeStackAdapter(List<MeetUser> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public TravelBuddyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_meet_swipe, parent, false);
        return new TravelBuddyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TravelBuddyViewHolder holder, int position) {
        final MeetUser user = data.get(position);

        /***Set User Data***/
        holder.tv_user_name.setText(user.getName() + ",");
        holder.tv_friends_count.setText("(" + user.getFriends_count() + " friends)");
        if (user.getPronouns() != null) {
            holder.tv_pronoun_1.setText(user.getPronouns());
            holder.tv_pronoun_2.setText(user.getPronouns());
        }
        if (user.getGenderIdentity() != null) {
            holder.tv_gender_s.setText(user.getGenderIdentity());
            holder.tv_gender_2.setText(user.getGenderIdentity());
        }
        if (user.getBio() != null)
            holder.tv_bio.setText(user.getBio());
        /***CURIOSITIES**/
        if (user.getRelationshipStatus() != null)
            holder.tv_relationship_status.setText(user.getRelationshipStatus());
        if (user.getHeight() != null)
            holder.tv_height.setText(user.getHeight());
        if (user.getLookingFor() != null)
            holder.tv_looking_for.setText(user.getLookingFor());
        if (user.getSmoke() != null)
            holder.tv_cigerate.setText(user.getSmoke());
        if (user.getDrink() != null)
            holder.tv_drink.setText(user.getDrink());
        if (user.getCannabis() != null)
            holder.tv_canabiese.setText(user.getCannabis());
        if (user.getPoliticalViews() != null)
            holder.tv_political_views.setText(user.getPoliticalViews());
        if (user.getReligion() != null)
            holder.tv_religion.setText(user.getReligion());
        if (user.getDietLike() != null)
            holder.tv_diet.setText(user.getDietLike());
        if (user.getSign() != null)
            holder.tv_sign.setText(user.getSign());
        if (user.getPets() != null)
            holder.tv_pet.setText(user.getPets());
        if (user.getKids() != null)
            holder.tv_children.setText(user.getKids());
        Glide
                .with(context)
                .load(user.getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_place_holder_image)
                .into(holder.img_user);

        holder.tv_user_age.setText(String.valueOf(user.getAge()));
        if (user.getShow_location() == 1) {
            holder.tv_distance.setText(user.getDistance());
            holder.tv_distance2.setText(user.getDistance());
        } else {
            holder.tv_distance.setVisibility(View.GONE);
            holder.tv_distance2.setVisibility(View.GONE);
        }
        /******/

        if (position == data.size()) {
            if (iListEnd != null) {
                iListEnd.onListEndListener();
            }
        }
        holder.img_like_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onLikeUserClick(user,position);
                }
            }
        });
        holder.img_dislike_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onDislikeUserClick(user,position);
                }
            }
        });


        holder.img_follow_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onFollowUserClick(user,position);
                }
            }
        });
        holder.btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onFollowUserClick(user,position);
                }
            }
        });

        holder.img_close_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                holder.rl_detail_view.setVisibility(View.GONE);
                holder.ll_normal_view.setVisibility(View.VISIBLE);
            }
        });

        holder.img_open_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                holder.rl_detail_view.setVisibility(View.VISIBLE);
                holder.ll_normal_view.setVisibility(View.GONE);
            }
        });
        holder.img_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.scrollView.smoothScrollTo(0, 0);
                if (iClick != null) {
                    iClick.onBlockUserClick(user, position);
                }
            }
        });
        holder.img_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onReportUserClick(user,position);
                }
            }
        });
        holder.btn_view_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpecificUserPosts.class);
                intent.putExtra(Constants.ID, user.getId().toString());
                context.startActivity(intent);
            }
        });
        holder.btn_view_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpecificUserFriendsActivity.class);
                intent.putExtra(Constants.ID, user.getId().toString());
                context.startActivity(intent);
            }
        });

        holder.img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iClick != null) {
                    iClick.onShareUserProfile(user,position);
                }
            }
        });


        if (user.getFacebook() == null) {

            ImageViewCompat.setImageTintList(holder.fb_img, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        }
        if (user.getTiktok() == null) {

            ImageViewCompat.setImageTintList(holder.tiktokimg, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        }
        if (user.getInstagram() == null) {
            holder.insta_img.setImageResource(R.drawable.ic_insta_grey);
        }
        holder.fb_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.getFacebook() == null) {
                    Toast.makeText(context, "Facebook link not found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(user.getFacebook());


            }
        });

        holder.insta_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getInstagram() == null) {
                    Toast.makeText(context, "Instagram link not found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(user.getFacebook());

            }
        });

        holder.tiktokimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getTiktok() == null) {
                    Toast.makeText(context, "Tiktok link not found!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(user.getFacebook());

            }
        });


    }

    private void reDirectTOChrome(String url) {


        try {
            Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            Toast.makeText(context, "Google Chrome is not installed on your device!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TravelBuddyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_open_details, img_close_details;
        private LinearLayout ll_normal_view;
        private RelativeLayout rl_detail_view;
        private SelectableRoundedImageView img_user;
        private TextView tv_user_name, tv_user_age, tv_distance, tv_gender_s, tv_gender_2, tv_pronoun_1, tv_pronoun_2, tv_distance2, tv_bio, tv_friends_count;
        private Button btn_view_posts, btn_view_friends, btn_add_friend;
        private TextView tv_relationship_status, tv_height, tv_looking_for, tv_cigerate, tv_drink, tv_canabiese, tv_political_views, tv_religion, tv_diet, tv_sign, tv_pet, tv_children;
        private ImageView img_like_user, img_dislike_user, img_follow_user, img_block, img_report, img_share;
        private ScrollView scrollView;
        private ImageView fb_img, insta_img, tiktokimg;

        public TravelBuddyViewHolder(View itemView) {
            super(itemView);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_user_age = itemView.findViewById(R.id.tv_user_age);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_gender_s = itemView.findViewById(R.id.tv_gender_s);
            tv_gender_2 = itemView.findViewById(R.id.tv_gender_2);
            tv_pronoun_1 = itemView.findViewById(R.id.tv_pronoun_1);
            tv_pronoun_2 = itemView.findViewById(R.id.tv_pronoun_2);
            tv_distance2 = itemView.findViewById(R.id.tv_distance2);
            tv_bio = itemView.findViewById(R.id.tv_bio);
            tv_friends_count = itemView.findViewById(R.id.tv_friends_count);

            tv_relationship_status = itemView.findViewById(R.id.tv_relationship_status);
            tv_height = itemView.findViewById(R.id.tv_height);
            tv_looking_for = itemView.findViewById(R.id.tv_looking_for);
            tv_cigerate = itemView.findViewById(R.id.tv_cigerate);
            tv_drink = itemView.findViewById(R.id.tv_drink);
            tv_canabiese = itemView.findViewById(R.id.tv_canabiese);
            tv_political_views = itemView.findViewById(R.id.tv_political_views);
            tv_religion = itemView.findViewById(R.id.tv_religion);
            tv_diet = itemView.findViewById(R.id.tv_diet);
            tv_sign = itemView.findViewById(R.id.tv_sign);
            tv_pet = itemView.findViewById(R.id.tv_pet);
            tv_children = itemView.findViewById(R.id.tv_children);

            btn_view_posts = itemView.findViewById(R.id.btn_view_posts);
            btn_view_friends = itemView.findViewById(R.id.btn_view_friends);
            btn_add_friend = itemView.findViewById(R.id.btn_add_friend);

            img_open_details = itemView.findViewById(R.id.img_open_details);
            img_close_details = itemView.findViewById(R.id.img_close_details);
            img_like_user = itemView.findViewById(R.id.img_like_user);
            img_dislike_user = itemView.findViewById(R.id.img_dislike_user);
            img_follow_user = itemView.findViewById(R.id.img_follow_user);
            img_block = itemView.findViewById(R.id.img_block);
            img_report = itemView.findViewById(R.id.img_report);
            img_share = itemView.findViewById(R.id.img_share);

            ll_normal_view = itemView.findViewById(R.id.ll_normal_view);
            rl_detail_view = itemView.findViewById(R.id.rl_detail_view);

            img_user = itemView.findViewById(R.id.img_user);
            scrollView = itemView.findViewById(R.id.scrollView);


            fb_img = itemView.findViewById(R.id.fb_img);
            insta_img = itemView.findViewById(R.id.insta_img);
            tiktokimg = itemView.findViewById(R.id.tiktokimg);
        }
    }
}
