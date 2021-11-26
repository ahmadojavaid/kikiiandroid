package com.jobesk.kikiiapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.jobesk.kikiiapp.Adapters.ImageAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackGetProfile;
import com.jobesk.kikiiapp.Model.AdsImagesModel.Image;
import com.jobesk.kikiiapp.Model.MeetUser;
import com.jobesk.kikiiapp.Model.ProfilePic;
import com.jobesk.kikiiapp.Model.ProfileUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;

import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SaveArrayListAds;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.UtilityFunctions;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyProfileActivity";
    private Context context = MyProfileActivity.this;
    private Activity activity = MyProfileActivity.this;
    public static final int REQUEST_EDIT_PROFILE = 230;
    private SelectableRoundedImageView img_user;
    private TextView tv_user_name, tv_user_age, tv_gender, tv_pronouns, tv_distance, tv_bio, tv_friends_count;
    private TextView tv_relationship_status, tv_height, tv_looking_for, tv_cigerate, tv_drink, tv_canabiese, tv_political_views, tv_religion, tv_diet, tv_sign, tv_pet, tv_children;
    private RecyclerView rv_curiosities;
    private Button btn_view_friends;
    private ImageView img_edit;
    private CustomLoader customLoader;
    private SessionManager sessionManager;
    private Call<CallbackGetProfile> callbackGetProfileCall;
    private CallbackGetProfile responseGetProfile;
    private List<Image> arrayListAds = new ArrayList<>();
    int currentPage = 0;
    ViewPager viewPager;
    LinearLayout pagerCon;
    ImageView shareImg;
    private Uri userProfileLink;
    private ImageView fb_img, insta_img, tiktok_img;
    String FBLink = "", instaLink = "", tiktokLInk = "";
    List<String> arrayListImages = new ArrayList<>();
    ImageAdapter adapterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initComponents();
        getUserProfile();

        img_edit.setOnClickListener(this);
        btn_view_friends.setOnClickListener(this);

    }

    private void getUserProfile() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());
        callbackGetProfileCall = api.getProfile(sessionManager.getAccessToken(), String.valueOf(0));
        callbackGetProfileCall.enqueue(new Callback<CallbackGetProfile>() {
            @Override
            public void onResponse(Call<CallbackGetProfile> call, Response<CallbackGetProfile> response) {
                Log.d(TAG, "onResponseProfile: " + new Gson().toJson(response.body()));
                responseGetProfile = response.body();
                if (responseGetProfile != null) {
                    if (response.isSuccessful()) {
                        customLoader.hideIndicator();
                        //Toast.makeText(context, responseGetProfile.getMessage(), Toast.LENGTH_SHORT).show();


                        if (responseGetProfile.getUser().getTiktok() != null) {
                            String tiktok = responseGetProfile.getUser().getTiktok();

                        }
                        if (responseGetProfile.getUser().getFacebook() != null) {
                            String fb = responseGetProfile.getUser().getFacebook();

                        }
                        if (responseGetProfile.getUser().getInstagram() != null) {
                            String instagram = responseGetProfile.getUser().getInstagram();

                        }


                        setData();
                    } else {
                        Log.d(TAG, "onResponse: " + responseGetProfile.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseGetProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackGetProfile> call, Throwable t) {
                if (!call.isCanceled()) {

                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();

                }
            }
        });
    }

    private void setData() {


        sessionManager.saveProfileUser(responseGetProfile.getUser());
        ProfileUser user = responseGetProfile.getUser();

        tiktokLInk = user.getTiktok();
        instaLink = user.getInstagram();
        FBLink = user.getFacebook();


        if (user.getFacebook() == null) {
            ImageViewCompat.setImageTintList(fb_img, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        } else {
            ImageViewCompat.setImageTintList(fb_img, ColorStateList.valueOf(context.getResources().getColor(R.color.fb_blue)));
        }

        if (user.getTiktok() == null) {
            ImageViewCompat.setImageTintList(tiktok_img, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        } else {
            ImageViewCompat.setImageTintList(tiktok_img, ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
        }

        if (user.getInstagram() == null) {
            insta_img.setImageResource(R.drawable.ic_insta_grey);
        } else {
            insta_img.setImageResource(R.drawable.insta_logo_medium);
        }

        tv_user_name.setText(user.getName());
        tv_friends_count.setText("(" + user.getFriends_count() + ")");
        if (user.getPronouns() != null)
            tv_pronouns.setText(user.getPronouns());
        if (user.getGenderIdentity() != null)
            tv_gender.setText(user.getGenderIdentity());
       /* if (user.getPronouns() != null)
            tv_distance.setText(user.getPronouns());
        if (user.getPronouns() != null)
            tv_bio.setText(user.getBio());*/
        if (user.getBio() != null)
            tv_bio.setText(user.getBio());
        /***CURIOSITIES**/
        if (user.getRelationshipStatus() != null)
            tv_relationship_status.setText(user.getRelationshipStatus());
        if (user.getHeight() != null) {
            String userHeight = user.getHeight();
            String value = "";
            String unit = sessionManager.getHeightUnit();
            if (unit.equalsIgnoreCase("Feet")) {
                value = userHeight;
            } else {
                value = feetToCentimeter(userHeight) + " cm";
            }
            tv_height.setText(value);
        }

        if (user.getLookingFor() != null)
            tv_looking_for.setText(user.getLookingFor());
        if (user.getSmoke() != null)
            tv_cigerate.setText(user.getSmoke());
        if (user.getDrink() != null)
            tv_drink.setText(user.getDrink());
        if (user.getCannabis() != null)
            tv_canabiese.setText(user.getCannabis());
        if (user.getPoliticalViews() != null)
            tv_political_views.setText(user.getPoliticalViews());
        if (user.getReligion() != null)
            tv_religion.setText(user.getReligion());
        if (user.getDietLike() != null)
            tv_diet.setText(user.getDietLike());
        if (user.getSign() != null)
            tv_sign.setText(user.getSign());
        if (user.getPets() != null)
            tv_pet.setText(user.getPets());
        if (user.getKids() != null)
            tv_children.setText(user.getKids());
//        Glide
//                .with(context)
//                .load(user.getProfilePic())
//                .centerCrop()
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .centerCrop()
//                .placeholder(R.drawable.ic_user_dummy)
//                .into(img_user);


        tv_user_age.setText(", " + UtilityFunctions.getAge(user.getBirthday()));


        if (arrayListImages.size() > 0) {
            arrayListImages.clear();
            adapterView.notifyDataSetChanged();
        }

        String profilePic1 = user.getProfilePic();
        arrayListImages.add(profilePic1);

        List<ProfilePic> listProfiles = user.getProfilePics();

        for (int i = 0; i < listProfiles.size(); i++) {
            arrayListImages.add(listProfiles.get(i).getPath());

        }
        viewpagerImages();
    }

    private void viewpagerImages() {


        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        adapterView = new ImageAdapter(this, arrayListImages);
        mViewPager.setAdapter(adapterView);


    }


    public class ImageAdapter extends PagerAdapter {

        Context context;

        LayoutInflater mLayoutInflater;


        public ImageAdapter(Context context, List<String> arrayListImages) {
            this.context = context;
//            this.arrayListImages = arrayListImages;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayListImages.size();
        }

//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == ((LinearLayout) object);
//        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.page_viewer_pics, container, false);

            String imagePos = arrayListImages.get(position);
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

    public String feetToCentimeter(String feet) {
        double dCentimeter = 0d;
        if (!TextUtils.isEmpty(feet)) {
            if (feet.contains("'")) {
                String tempfeet = feet.substring(0, feet.indexOf("'"));
                if (!TextUtils.isEmpty(tempfeet)) {
                    dCentimeter += ((Double.valueOf(tempfeet)) * 30.48);
                }
            }
            if (feet.contains("\"")) {
                String tempinch = feet.substring(feet.indexOf("'") + 1, feet.indexOf("\""));
                if (!TextUtils.isEmpty(tempinch)) {
                    dCentimeter += ((Double.valueOf(tempinch)) * 2.54);
                }
            }
        }
        return String.valueOf(dCentimeter);
        //Format to decimal digit as per your requirement
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        img_user = findViewById(R.id.img_user);
        img_edit = findViewById(R.id.img_edit);

        tv_bio = findViewById(R.id.tv_bio);
        tv_distance = findViewById(R.id.tv_distance);
        tv_gender = findViewById(R.id.tv_gender_2);
        tv_pronouns = findViewById(R.id.tv_pronoun_2);
        tv_friends_count = findViewById(R.id.tv_friends_count);
        tv_user_age = findViewById(R.id.tv_user_age);
        tv_user_name = findViewById(R.id.tv_user_name);

        tv_relationship_status = findViewById(R.id.tv_relationship_status);
        tv_height = findViewById(R.id.tv_height);
        tv_looking_for = findViewById(R.id.tv_looking_for);
        tv_cigerate = findViewById(R.id.tv_cigerate);
        tv_drink = findViewById(R.id.tv_drink);
        tv_canabiese = findViewById(R.id.tv_canabiese);
        tv_political_views = findViewById(R.id.tv_political_views);
        tv_religion = findViewById(R.id.tv_religion);
        tv_diet = findViewById(R.id.tv_diet);
        tv_sign = findViewById(R.id.tv_sign);
        tv_pet = findViewById(R.id.tv_pet);
        tv_children = findViewById(R.id.tv_children);

        btn_view_friends = findViewById(R.id.btn_view_friends);


        shareImg = findViewById(R.id.shareImg);
        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShareLink();
            }
        });


        fb_img = findViewById(R.id.fb_img);
        insta_img = findViewById(R.id.insta_img);
        tiktok_img = findViewById(R.id.tiktok_img);

        fb_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FBLink == null) {
                    Toast.makeText(context, "You have not added your Facebook link!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(getApplicationContext().getResources().getString(R.string.fb_link) + "" + FBLink);

            }
        });


        insta_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (instaLink == null) {
                    Toast.makeText(context, "You have not added your Instagram link!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(getApplicationContext().getResources().getString(R.string.insta_link) + "" + instaLink);

            }
        });
        tiktok_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tiktokLInk == null) {
                    Toast.makeText(context, "You have not added your Tiktok link!", Toast.LENGTH_SHORT).show();
                    return;
                }
                reDirectTOChrome(getApplicationContext().getResources().getString(R.string.tiktok_link) + "" + tiktokLInk);

            }
        });


    }

    private void reDirectTOChrome(String url) {


        try {
            Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            Toast.makeText(context, "Google Chrome is not installed on your device!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_edit:
                startActivityForResult(new Intent(context, EditProfileActivity.class), REQUEST_EDIT_PROFILE);
                break;
            case R.id.btn_view_friends:
                startActivity(new Intent(context, FriendsActivity.class));
                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_PROFILE && resultCode == RESULT_OK) {
            getUserProfile();


        }
    }

    private void createShareLink() {
        String userID = sessionManager.getUserID();

        String packageName = activity.getPackageName();
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store/apps/?user_id=" + userID))
                .setDynamicLinkDomain("kikiiapp1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
//                                .setMinimumVersion(125)
                                .build())
                // Open links with com.example.ios on iOS
                .buildDynamicLink();
        userProfileLink = dynamicLink.getUri();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(userProfileLink)
                .buildShortDynamicLink()
                .addOnCompleteListener(MyProfileActivity.this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            customLoader.hideIndicator();
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.d(TAG, "onComplete: " + shortLink);
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            startActivity(Intent.createChooser(intent, "Share"));
                        } else {
                            customLoader.hideIndicator();
                            Log.d(TAG, "ERROR: " + task.getException());
                        }
                    }
                });
    }
}
