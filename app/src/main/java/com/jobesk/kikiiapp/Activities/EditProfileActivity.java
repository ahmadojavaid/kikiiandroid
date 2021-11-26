package com.jobesk.kikiiapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.jobesk.kikiiapp.Activities.Profile.AddBioActivity;
import com.jobesk.kikiiapp.Activities.Profile.DoYouDrinkActivity;
import com.jobesk.kikiiapp.Activities.Profile.DoYouSmokeActivity;
import com.jobesk.kikiiapp.Activities.Profile.DoYouUseCannabisActivity;
import com.jobesk.kikiiapp.Activities.Profile.GenderIdentityActivity;
import com.jobesk.kikiiapp.Activities.Profile.LookingForActivity;
import com.jobesk.kikiiapp.Activities.Profile.PoliticalViewsActivity;
import com.jobesk.kikiiapp.Activities.Profile.PronounsActivity;
import com.jobesk.kikiiapp.Activities.Profile.RelationshipStatusActivity;
import com.jobesk.kikiiapp.Activities.Profile.SetHeightActivity;
import com.jobesk.kikiiapp.Activities.Profile.SexualIdentityActivity;
import com.jobesk.kikiiapp.Activities.Profile.YourDietActivity;
import com.jobesk.kikiiapp.Activities.Profile.YourKidsActivity;
import com.jobesk.kikiiapp.Activities.Profile.YourPetsActivity;
import com.jobesk.kikiiapp.Activities.Profile.YourReligionActivity;
import com.jobesk.kikiiapp.Activities.Profile.YourSignActivity;
import com.jobesk.kikiiapp.Activities.SignUpModule.SubscriptionActivityApp;
import com.jobesk.kikiiapp.Activities.SignUpModule.SubscriptionActivitySignUp;
import com.jobesk.kikiiapp.Adapters.CuriositiesAdapter;
import com.jobesk.kikiiapp.Callbacks.CallbackUpdateProfile;
import com.jobesk.kikiiapp.Model.CuriosityChipModel;
import com.jobesk.kikiiapp.Model.ProfilePic;
import com.jobesk.kikiiapp.Model.ProfileUser;
import com.jobesk.kikiiapp.Netwrok.API;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.Netwrok.RestAdapter;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.CommonMethods;
import com.jobesk.kikiiapp.Utils.CustomLoader;
import com.jobesk.kikiiapp.Utils.SelectImage;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.jobesk.kikiiapp.Utils.ShowDialogues;
import com.jobesk.kikiiapp.Utils.ShowSelectImageBottomSheet;
import com.joooonho.SelectableRoundedImageView;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.OnChipClickListener;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, OnChipClickListener {

    private static final String TAG = "EditProfileActivity";
    private Context context = EditProfileActivity.this;
    private Activity activity = EditProfileActivity.this;

    public static final int REQUEST_GENDER_IDENTITY = 400;
    public static final int REQUEST_SEXUAL_IDENTITY = 410;
    public static final int REQUEST_PRONOUNS = 420;
    public static final int REQUEST_ADD_BIO = 430;
    public static final int REQUEST_RELATIONSHIP_STATUS = 440;
    public static final int REQUEST_HEIGHT = 450;
    public static final int REQUEST_LOOKING_FOR = 460;
    public static final int REQUEST_DO_YOU_DRINK = 470;
    public static final int REQUEST_DO_YOU_SMOKE = 480;
    public static final int REQUEST_CANNABIS = 490;
    public static final int REQUEST_POLITICAL_VIEWS = 500;
    public static final int REQUEST_RELIGION = 510;
    public static final int REQUEST_DIET_LIKE = 520;
    public static final int REQUEST_YOUR_SIGN = 530;
    public static final int REQUEST_PETS = 540;
    public static final int REQUEST_KIDS = 550;

    private SelectableRoundedImageView img_user, img_selected_1, img_selected_2, img_selected_3, img_selected_4, img_selected_5, img_selected_6, img_selected_7, img_selected_8;
    private ImageView img_select_1, img_select_2, img_select_3, img_select_4, img_select_5, img_select_6, img_select_7, img_select_8, img_ok;
    private static int setImageOn = 0;
    private LinearLayout ll_gender_identity, ll_sexual_identity, ll_pronouns, ll_bio;

    private List<String> mediaPaths = new ArrayList<>();
    private List<MultipartBody.Part> imagesList = new ArrayList<>();
    private ProfileUser user;
    private SessionManager sessionManager;
    private CustomLoader customLoader;

    private List<Chip> chipList = new ArrayList<>();
    private CuriositiesAdapter curiositiesAdapter;
    private ChipView chip_curiosities;

    private TextView tv_gender_identity, tv_sexual_identity, tv_pronouns, tv_bio;

    private Map<String, String> updateProfileParams = new HashMap<>();

    private Call<CallbackUpdateProfile> callbackUpdateProfile;
    private CallbackUpdateProfile responseUpdateProfile;

    private Map<String, RequestBody> editProfileMultipartParams = new HashMap<>();
    private ImageView img_back;
    private Switch swch_location, swch_incognito;
    boolean isPaid;
    private ImageView facebook_img, insta_img, ticktok_img;
    private String FbLink = "", tiktokLink = "", instaLink;
    private ImageView del_img_1, del_img_2, del_img_3, del_img_4, del_img_5, del_img_6, del_img_7, del_img_8;

    List<ProfilePic> profilePics;

    private ArrayList<String> removeIdz = new ArrayList<>();
    private boolean profileUpdated = false;
    private String profileUpdatedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initComponents();
        setData();

        chip_curiosities.setOnChipClickListener(this);

        img_select_1.setOnClickListener(this);
        img_select_2.setOnClickListener(this);
        img_select_3.setOnClickListener(this);
        img_select_4.setOnClickListener(this);
        img_select_5.setOnClickListener(this);
        img_select_6.setOnClickListener(this);
        img_select_7.setOnClickListener(this);
        img_select_8.setOnClickListener(this);

        img_ok.setOnClickListener(this);
        img_back.setOnClickListener(this);

        img_user.setOnClickListener(this);

        ll_gender_identity.setOnClickListener(this);
        ll_sexual_identity.setOnClickListener(this);
        ll_pronouns.setOnClickListener(this);
        ll_bio.setOnClickListener(this);


        sessionManager = new SessionManager(getApplicationContext());
        isPaid = sessionManager.getIsPaid();
//        if (isPaid) {
//            swch_location.setClickable(true);
//            swch_incognito.setClickable(true);
//        } else {
//
//            swch_location.setClickable(false);
//            swch_incognito.setClickable(false);
//            swch_location.setEnabled(false);
//            swch_incognito.setEnabled(false);
//
//
//            Intent intent = new Intent(EditProfileActivity.this, SubscriptionActivitySignUp.class);
//            intent.putExtra("value", "1");
//            startActivity(intent);
//
//        }


        swch_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isPaid) {
                        updateProfileParams.put(Constants.SHOW_LOCATION, "1");
                    } else {
                        swch_location.setChecked(false);
                        Intent intent = new Intent(EditProfileActivity.this, SubscriptionActivityApp.class);
                        intent.putExtra("from", "editProfile");
                        startActivityForResult(intent, 1);
                    }
                } else {
                    if (isPaid) {
                        updateProfileParams.put(Constants.SHOW_LOCATION, "0");
                    } else {
                        swch_location.setChecked(true);
                        Intent intent = new Intent(EditProfileActivity.this, SubscriptionActivityApp.class);
                        intent.putExtra("from", "editProfile");
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });

        swch_incognito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isPaid) {
                        updateProfileParams.put(Constants.INCOGNITO, "1");
                    } else {
                        swch_incognito.setChecked(true);
                        Intent intent = new Intent(EditProfileActivity.this, SubscriptionActivityApp.class);
                        intent.putExtra("from", "editProfile");
                        startActivityForResult(intent, 1);
                    }
                } else {
                    if (isPaid) {
                        updateProfileParams.put(Constants.INCOGNITO, "0");
                    } else {
                        swch_incognito.setChecked(false);
                        Intent intent = new Intent(EditProfileActivity.this, SubscriptionActivityApp.class);
                        intent.putExtra("from", "editProfile");
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });


    }

    private void setData() {
        Glide.with(context)
                .load(user.getProfilePic())
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_user_dummy)
                .into(img_user);

        profilePics = user.getProfilePics();


        if (profilePics.size() > 0) {
            for (int i = 0; i < profilePics.size(); i++) {
                profilePics.get(i).setType("jpg");
                setUrlToImage(profilePics.get(i).getPath(), i + 1);
            }
        }

        if (user.getGenderIdentity() != null)
            tv_gender_identity.setText(user.getGenderIdentity());
        if (user.getSexualIdentity() != null)
            tv_sexual_identity.setText(user.getSexualIdentity().toString());
        if (user.getPronouns() != null)
            tv_pronouns.setText(user.getPronouns());
        if (user.getBio() != null)
            tv_bio.setText(user.getBio());

        if (user.getShow_location() == 1)
            swch_location.setChecked(true);
        else
            swch_location.setChecked(false);

        if (user.getIncognito() == 1)
            swch_incognito.setChecked(true);
        else
            swch_incognito.setChecked(false);

        setCuriosities();
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

    public String centimeterToFeet(String centemeter) {
        int feetPart = 0;
        int inchesPart = 0;
        if (!TextUtils.isEmpty(centemeter)) {
            double dCentimeter = Double.valueOf(centemeter);
            feetPart = (int) Math.floor((dCentimeter / 2.54) / 12);
            System.out.println((dCentimeter / 2.54) - (feetPart * 12));
            inchesPart = (int) Math.ceil((dCentimeter / 2.54) - (feetPart * 12));
        }
        return String.format("%d' %d''", feetPart, inchesPart);

    }


    private void setCuriosities() {
        if (user.getRelationshipStatus() != null)
            chipList.add(new CuriosityChipModel(user.getRelationshipStatus(), 1));
        else
            chipList.add(new CuriosityChipModel("RelationShip Status?", 1));

        if (user.getHeight() != null) {
            String userHeight = user.getHeight();
            String unit = sessionManager.getHeightUnit();

            String value = userHeight;

            if (unit.equalsIgnoreCase("Feet")) {

                value = userHeight;
            } else {

                value = feetToCentimeter(value) + " cm";


            }

            chipList.add(new CuriosityChipModel(value, 2));
        } else
            chipList.add(new CuriosityChipModel("Height?", 2));


        if (user.getLookingFor() != null)
            chipList.add(new CuriosityChipModel(user.getLookingFor(), 3));
        else
            chipList.add(new CuriosityChipModel("Looking For?", 3));
        if (user.getDrink() != null)
            chipList.add(new CuriosityChipModel(user.getDrink(), 4));
        else
            chipList.add(new CuriosityChipModel("Do you drink?", 4));
        if (user.getSmoke() != null)
            chipList.add(new CuriosityChipModel(user.getSmoke(), 5));
        else
            chipList.add(new CuriosityChipModel("Do you smoke?", 5));
        if (user.getCannabis() != null)
            chipList.add(new CuriosityChipModel(user.getCannabis(), 6));
        else
            chipList.add(new CuriosityChipModel("Do you use Cannabis?", 6));
        if (user.getPoliticalViews() != null)
            chipList.add(new CuriosityChipModel(user.getPoliticalViews(), 7));
        else
            chipList.add(new CuriosityChipModel("Political views?", 7));
        if (user.getReligion() != null)
            chipList.add(new CuriosityChipModel(user.getReligion(), 8));
        else
            chipList.add(new CuriosityChipModel("Your religion?", 8));
        if (user.getDietLike() != null)
            chipList.add(new CuriosityChipModel(user.getDietLike(), 9));
        else
            chipList.add(new CuriosityChipModel("Diet like?", 9));
        if (user.getSign() != null)
            chipList.add(new CuriosityChipModel(user.getSign(), 10));
        else
            chipList.add(new CuriosityChipModel("Your sign?", 10));
        if (user.getPets() != null)
            chipList.add(new CuriosityChipModel(user.getPets(), 11));
        else
            chipList.add(new CuriosityChipModel("Any pets?", 11));
        if (user.getKids() != null)
            chipList.add(new CuriosityChipModel(user.getKids(), 12));
        else
            chipList.add(new CuriosityChipModel("Want Kids?", 12));

        curiositiesAdapter = new CuriositiesAdapter(context);
        chip_curiosities.setAdapter(curiositiesAdapter);
        chip_curiosities.setChipList(chipList);
    }

    private void initComponents() {
        customLoader = new CustomLoader(this, false);
        sessionManager = new SessionManager(this);

        user = sessionManager.getProfileUser();

        tv_gender_identity = findViewById(R.id.tv_gender_identity);
        tv_sexual_identity = findViewById(R.id.tv_sexual_identity);
        tv_pronouns = findViewById(R.id.tv_pronoun_2);
        tv_bio = findViewById(R.id.tv_bio);

        ll_gender_identity = findViewById(R.id.ll_gender_identity);
        ll_sexual_identity = findViewById(R.id.ll_sexual_identity);
        ll_pronouns = findViewById(R.id.ll_pronouns);
        ll_bio = findViewById(R.id.ll_bio);

        img_user = findViewById(R.id.img_user);
        img_selected_1 = findViewById(R.id.img_selected_1);
        img_selected_2 = findViewById(R.id.img_selected_2);
        img_selected_3 = findViewById(R.id.img_selected_3);
        img_selected_4 = findViewById(R.id.img_selected_4);
        img_selected_5 = findViewById(R.id.img_selected_5);
        img_selected_6 = findViewById(R.id.img_selected_6);
        img_selected_7 = findViewById(R.id.img_selected_7);
        img_selected_8 = findViewById(R.id.img_selected_8);

        img_select_1 = findViewById(R.id.img_select_1);
        img_select_2 = findViewById(R.id.img_select_2);
        img_select_3 = findViewById(R.id.img_select_3);
        img_select_4 = findViewById(R.id.img_select_4);
        img_select_5 = findViewById(R.id.img_select_5);
        img_select_6 = findViewById(R.id.img_select_6);
        img_select_7 = findViewById(R.id.img_select_7);
        img_select_8 = findViewById(R.id.img_select_8);


        del_img_1 = findViewById(R.id.del_img_1);
        del_img_2 = findViewById(R.id.del_img_2);
        del_img_3 = findViewById(R.id.del_img_3);
        del_img_4 = findViewById(R.id.del_img_4);
        del_img_5 = findViewById(R.id.del_img_5);
        del_img_6 = findViewById(R.id.del_img_6);
        del_img_7 = findViewById(R.id.del_img_7);
        del_img_8 = findViewById(R.id.del_img_8);


        del_img_1.setVisibility(View.GONE);
        del_img_2.setVisibility(View.GONE);
        del_img_3.setVisibility(View.GONE);
        del_img_4.setVisibility(View.GONE);
        del_img_5.setVisibility(View.GONE);
        del_img_6.setVisibility(View.GONE);
        del_img_7.setVisibility(View.GONE);
        del_img_8.setVisibility(View.GONE);


        img_ok = findViewById(R.id.img_ok);
        img_back = findViewById(R.id.img_back);

        chip_curiosities = findViewById(R.id.chip_curiosities);

        swch_location = findViewById(R.id.swch_location);
        swch_incognito = findViewById(R.id.swch_incognito);


        facebook_img = findViewById(R.id.facebook_img);
        insta_img = findViewById(R.id.insta_img);
        ticktok_img = findViewById(R.id.ticktok_img);


        if (user.getFacebook() == null) {
            ImageViewCompat.setImageTintList(facebook_img, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        } else {
            ImageViewCompat.setImageTintList(facebook_img, ColorStateList.valueOf(context.getResources().getColor(R.color.fb_blue)));
        }

        if (user.getTiktok() == null) {
            ImageViewCompat.setImageTintList(ticktok_img, ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));
        } else {
            ImageViewCompat.setImageTintList(ticktok_img, ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
        }

        if (user.getInstagram() == null) {
            insta_img.setImageResource(R.drawable.ic_insta_grey);
        } else {
            insta_img.setImageResource(R.drawable.insta_logo_medium);
        }


        facebook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFb();
            }
        });
        insta_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInsta();
            }
        });

        ticktok_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTiktok();
            }
        });

        delImagesClick();
    }


    private void delImagesClick() {

        del_img_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_selected_1.setVisibility(View.GONE);
                del_img_1.setVisibility(View.GONE);
                img_select_1.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }

//                if (profilePics.get(0).getType().equalsIgnoreCase("jpg")) {
//
//                }

                try {
                    if (profilePics.get(0).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(0).getId()));
//                    profilePics.remove(0);
                        profilePics.get(0).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
        del_img_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_selected_2.setVisibility(View.GONE);
                del_img_2.setVisibility(View.GONE);
                img_select_2.setVisibility(View.VISIBLE);


                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }
//

                try {
                    if (profilePics.get(1).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(1).getId()));
//                    profilePics.remove(1);
                        profilePics.get(1).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
        del_img_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_3.setVisibility(View.GONE);
                del_img_3.setVisibility(View.GONE);
                img_select_3.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }


                try {
                    if (profilePics.get(2).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(2).getId()));
//                    profilePics.remove(2);
                        profilePics.get(2).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });

        del_img_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_4.setVisibility(View.GONE);
                del_img_4.setVisibility(View.GONE);
                img_select_4.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }

                try {
                    if (profilePics.get(3).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(3).getId()));
//                    profilePics.remove(3);
                        profilePics.get(3).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
        del_img_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_5.setVisibility(View.GONE);
                del_img_5.setVisibility(View.GONE);
                img_select_5.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }


                try {
                    if (profilePics.get(4).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(4).getId()));
//                    profilePics.remove(4);
                        profilePics.get(4).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
        del_img_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_6.setVisibility(View.GONE);
                del_img_6.setVisibility(View.GONE);
                img_select_6.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }

                try {
                    if (profilePics.get(5).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(5).getId()));
//                    profilePics.remove(5);
                        profilePics.get(5).setDeleted(true);
                    }
                } catch (Exception e) {

                }

            }
        });
        del_img_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_7.setVisibility(View.GONE);
                del_img_7.setVisibility(View.GONE);
                img_select_7.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }


                try {
                    if (profilePics.get(6).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(6).getId()));
//                    profilePics.remove(6);
                        profilePics.get(6).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
        del_img_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_selected_8.setVisibility(View.GONE);
                del_img_8.setVisibility(View.GONE);
                img_select_8.setVisibility(View.VISIBLE);

                if (mediaPaths.size() > 0) {
                    mediaPaths.remove(mediaPaths.size() - 1);
                }


                try {
                    if (profilePics.get(7).getPath().contains("https://")) {
                        removeIdz.add(String.valueOf(profilePics.get(7).getId()));
//                    profilePics.remove(7);
                        profilePics.get(7).setDeleted(true);
                    }
                } catch (Exception e) {

                }


            }
        });
    }


    public void addFb() {

        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_facebook_address, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);

        EditText edit_text = deleteDialogView.findViewById(R.id.edit_text);
        TextView cancel_tv = deleteDialogView.findViewById(R.id.cancel_tv);
        TextView save_tv = deleteDialogView.findViewById(R.id.save_tv);


        if (user.getFacebook() != null) {
            edit_text.setText(user.getFacebook());
        }


        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FbLink = edit_text.getText().toString().trim();

                if (FbLink.isEmpty()) {
                    Toast.makeText(context, "Please Enter Facebook Link", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateProfileParams.put("facebook", FbLink);
                deleteDialog.dismiss();
            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void addInsta() {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_insta_address, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);

        EditText edit_text = deleteDialogView.findViewById(R.id.edit_text);
        TextView cancel_tv = deleteDialogView.findViewById(R.id.cancel_tv);
        TextView save_tv = deleteDialogView.findViewById(R.id.save_tv);

        if (user.getInstagram() != null) {
            edit_text.setText(user.getInstagram());
        }


        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instaLink = edit_text.getText().toString().trim();

                if (instaLink.isEmpty()) {
                    Toast.makeText(context, "Please Enter Facebook Link", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateProfileParams.put("instagram", instaLink);
                deleteDialog.dismiss();
            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    public void addTiktok() {
        LayoutInflater factory = LayoutInflater.from(activity);
        final View deleteDialogView = factory.inflate(R.layout.dialog_tiktok_address, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(activity).create();
        deleteDialog.setView(deleteDialogView);

        EditText edit_text = deleteDialogView.findViewById(R.id.edit_text);
        TextView cancel_tv = deleteDialogView.findViewById(R.id.cancel_tv);
        TextView save_tv = deleteDialogView.findViewById(R.id.save_tv);

        if (user.getTiktok() != null) {
            edit_text.setText(user.getTiktok());
        }


        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiktokLink = edit_text.getText().toString().trim();

                if (tiktokLink.isEmpty()) {
                    Toast.makeText(context, "Please Enter Facebook Link", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateProfileParams.put("tiktok", tiktokLink);
                deleteDialog.dismiss();

            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_gender_identity:
                startActivityForResult(new Intent(context, GenderIdentityActivity.class), REQUEST_GENDER_IDENTITY);
                break;
            case R.id.ll_sexual_identity:
                startActivityForResult(new Intent(context, SexualIdentityActivity.class), REQUEST_SEXUAL_IDENTITY);
                break;
            case R.id.ll_pronouns:
                startActivityForResult(new Intent(context, PronounsActivity.class), REQUEST_PRONOUNS);
                break;
            case R.id.ll_bio:
                startActivityForResult(new Intent(context, AddBioActivity.class), REQUEST_ADD_BIO);
                break;
            case R.id.img_select_1:
                setImageOn = 1;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_1, Constants.SINGLE);
                break;
            case R.id.img_select_2:
                setImageOn = 2;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_2, Constants.SINGLE);
                break;
            case R.id.img_select_3:
                setImageOn = 3;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_3, Constants.SINGLE);
                break;
            case R.id.img_select_4:
                setImageOn = 4;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_4, Constants.SINGLE);
                break;
            case R.id.img_select_5:
                setImageOn = 5;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_5, Constants.SINGLE);
                break;
            case R.id.img_select_6:
                setImageOn = 6;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_6, Constants.SINGLE);
                break;
            case R.id.img_select_7:
                setImageOn = 7;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_7, Constants.SINGLE);
                break;
            case R.id.img_select_8:
                setImageOn = 8;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_select_8, Constants.SINGLE);
                break;
            case R.id.img_ok:
//                if (updateProfileParams.size() > 0) {
                if (mediaPaths.size() > 0) {
                    uploadFiles();
                } else {
                    updateProfile();
                }
//                }
                break;
            case R.id.img_back:
                CommonMethods.goBack(this);
                break;
            case R.id.img_user:

                setImageOn = -1;
                ShowSelectImageBottomSheet.showDialogForSelectMedia(this, img_user, Constants.SINGLE);

                break;
        }
    }

    private void uploadFiles() {


        if (removeIdz.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < removeIdz.size(); i++) {

                String idz = removeIdz.get(i);

                stringBuilder.append(idz).append(",");

            }

            String removeIdz = stringBuilder.toString();
            if (removeIdz.endsWith(",")) {
                removeIdz = removeIdz.substring(0, removeIdz.length() - 1);
            }

            updateProfileParams.put("deleted_pics", removeIdz);
        }


        for (int i = 0; i < mediaPaths.size(); i++) {
            imagesList.add(SelectImage.prepareFilePart("new_pics[]", mediaPaths.get(i)));
        }
        if (profileUpdated) {
            imagesList.add(SelectImage.prepareFilePart("profile_pic", profileUpdatedPath));
        }


        updateProfileWithImages();
    }

    private void updateProfileWithImages() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "createPost: " + sessionManager.getAccessToken());
        callbackUpdateProfile = api.updateProfileWithImages(sessionManager.getAccessToken(), updateProfileParams, imagesList);
        callbackUpdateProfile.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUpdateProfile = response.body();
                if (responseUpdateProfile != null) {
                    customLoader.hideIndicator();
                    if (responseUpdateProfile.getSuccess()) {
                        Log.d(TAG, "onResponse: " + responseUpdateProfile.getMessage());
                        Toast.makeText(context, responseUpdateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                        setResultBack();
                    } else {
                        Log.d(TAG, "onResponse: " + responseUpdateProfile.getMessage());
                        Toast.makeText(context, responseUpdateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }


    private void setResultBack() {
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }

    private void setUriToImage(Uri uri) {
        switch (setImageOn) {
            case -1:
                img_user.setImageURI(uri);
                break;
            case 1:
                img_selected_1.setImageURI(uri);
                img_select_1.setVisibility(View.GONE);
                del_img_1.setVisibility(View.VISIBLE);
                img_selected_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                img_selected_2.setImageURI(uri);
                img_select_2.setVisibility(View.GONE);
                del_img_2.setVisibility(View.VISIBLE);
                img_selected_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                img_selected_3.setImageURI(uri);
                img_select_3.setVisibility(View.GONE);
                del_img_3.setVisibility(View.VISIBLE);
                img_selected_3.setVisibility(View.VISIBLE);
                break;
            case 4:
                img_selected_4.setImageURI(uri);
                img_select_4.setVisibility(View.GONE);
                del_img_4.setVisibility(View.VISIBLE);
                img_selected_4.setVisibility(View.VISIBLE);
                break;
            case 5:
                img_selected_5.setImageURI(uri);
                img_select_5.setVisibility(View.GONE);
                del_img_5.setVisibility(View.VISIBLE);
                img_selected_5.setVisibility(View.VISIBLE);
                break;
            case 6:
                img_selected_6.setImageURI(uri);
                img_select_6.setVisibility(View.GONE);
                del_img_6.setVisibility(View.VISIBLE);
                img_selected_6.setVisibility(View.VISIBLE);
                break;
            case 7:
                img_selected_7.setImageURI(uri);
                img_select_7.setVisibility(View.GONE);
                del_img_7.setVisibility(View.VISIBLE);
                img_selected_7.setVisibility(View.VISIBLE);
                break;
            case 8:
                img_selected_8.setImageURI(uri);
                img_select_8.setVisibility(View.GONE);
                del_img_8.setVisibility(View.VISIBLE);
                img_selected_8.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void setUrlToImage(String path, int setImageOn) {
        switch (setImageOn) {
            case 1:
                Glide
                        .with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_1);
                img_select_1.setVisibility(View.GONE);
                del_img_1.setVisibility(View.VISIBLE);
                img_selected_1.setVisibility(View.VISIBLE);


                break;
            case 2:
                Glide
                        .with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_2);
                img_select_2.setVisibility(View.GONE);
                del_img_2.setVisibility(View.VISIBLE);
                img_selected_2.setVisibility(View.VISIBLE);
                break;
            case 3:
                Glide.with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_3);
                img_select_3.setVisibility(View.GONE);
                del_img_3.setVisibility(View.VISIBLE);
                img_selected_3.setVisibility(View.VISIBLE);
                break;
            case 4:

                Glide.with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_4);
                img_select_4.setVisibility(View.GONE);
                del_img_4.setVisibility(View.VISIBLE);
                img_selected_4.setVisibility(View.VISIBLE);
                break;
            case 5:

                Glide.with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_5);
                img_select_5.setVisibility(View.GONE);
                del_img_5.setVisibility(View.VISIBLE);
                img_selected_5.setVisibility(View.VISIBLE);
                break;
            case 6:

                Glide.with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_6);
                img_select_6.setVisibility(View.GONE);
                del_img_6.setVisibility(View.VISIBLE);
                img_selected_6.setVisibility(View.VISIBLE);
                break;
            case 7:

                Glide
                        .with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_7);
                img_select_7.setVisibility(View.GONE);
                del_img_7.setVisibility(View.VISIBLE);
                img_selected_7.setVisibility(View.VISIBLE);
                break;
            case 8:

                Glide
                        .with(context)
                        .load(path)
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_dummy)
                        .into(img_selected_8);
                img_select_8.setVisibility(View.GONE);
                del_img_8.setVisibility(View.VISIBLE);
                img_selected_8.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> temp = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (setImageOn == -1) {
                profileUpdated = true;
                profileUpdatedPath = temp.get(0);
                setUriToImage(Uri.parse(temp.get(0)));
            } else {
                if (setImageOn >= mediaPaths.size()) {
                    mediaPaths.add(temp.get(0));
                } else if (setImageOn < mediaPaths.size())
                    mediaPaths.add(setImageOn, temp.get(0));

                setUriToImage(Uri.parse(temp.get(0)));
            }

            Log.d("hhhh", "onActivityResult: " + mediaPaths.size());

        }
        if (requestCode == REQUEST_GENDER_IDENTITY && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.GENDER_IDENTITY);
                tv_gender_identity.setText(cat);
                updateProfileParams.put(Constants.GENDER_IDENTITY, cat);
            }
        }
        if (requestCode == REQUEST_SEXUAL_IDENTITY && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.SEXUAL_IDENTITY);
                tv_sexual_identity.setText(cat);
                updateProfileParams.put(Constants.SEXUAL_IDENTITY, cat);
            }
        }
        if (requestCode == REQUEST_PRONOUNS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.PRONOUNS);
                tv_pronouns.setText(cat);
                updateProfileParams.put(Constants.PRONOUNS, cat);
            }
        }
        if (requestCode == REQUEST_ADD_BIO && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.BIO);
                tv_bio.setText(cat);
                updateProfileParams.put(Constants.BIO, cat);
            }
        }
        if (requestCode == REQUEST_RELATIONSHIP_STATUS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.RELATIONSHIP_STATUS);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(0);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.RELATIONSHIP_STATUS, cat);
            }
        }
        if (requestCode == REQUEST_HEIGHT && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.HEIGHT);


                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(1);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.HEIGHT, cat);
            }
        }
        if (requestCode == REQUEST_LOOKING_FOR && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.LOOKING_FOR);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(2);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.LOOKING_FOR, cat);
            }
        }
        if (requestCode == REQUEST_DO_YOU_DRINK && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.DRINK);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(3);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.DRINK, cat);
            }
        }
        if (requestCode == REQUEST_CANNABIS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.CANNABIS);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(5);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.CANNABIS, cat);
            }
        }
        if (requestCode == REQUEST_POLITICAL_VIEWS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.POLITICAL_VIEWS);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(6);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.POLITICAL_VIEWS, cat);
            }
        }
        if (requestCode == REQUEST_RELIGION && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.RELIGION);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(7);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.RELIGION, cat);

            }
        }
        if (requestCode == REQUEST_DIET_LIKE && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.DIET_LIKE);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(8);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.DIET_LIKE, cat);
            }
        }
        if (requestCode == REQUEST_YOUR_SIGN && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.SIGN);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(9);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.SIGN, cat);
            }
        }
        if (requestCode == REQUEST_PETS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.PETS);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(10);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.PETS, cat);
            }
        }
        if (requestCode == REQUEST_DO_YOU_SMOKE && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.SMOKE);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(4);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.SMOKE, cat);
            }
        }
        if (requestCode == REQUEST_KIDS && resultCode == RESULT_OK) {
            if (data != null) {
                String cat = data.getStringExtra(Constants.KIDS);
                CuriosityChipModel chip = (CuriosityChipModel) chipList.get(11);
                chip.setmName(cat);
                curiositiesAdapter = new CuriositiesAdapter(context);
                chip_curiosities.setAdapter(curiositiesAdapter);
                chip_curiosities.setChipList(chipList);

                updateProfileParams.put(Constants.KIDS, cat);
            }
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                isPaid = sessionManager.getIsPaid();
            }
        }
    }

    @Override
    public void onChipClick(Chip chip) {

        CuriosityChipModel curiosityChipModel = (CuriosityChipModel) chip;
        if (curiosityChipModel != null) {
            Intent intent;
            switch (curiosityChipModel.getPosition()) {
                case 1:
                    intent = new Intent(context, RelationshipStatusActivity.class);
                    if (user.getRelationshipStatus() != null)
                        intent.putExtra(Constants.RELATIONSHIP_STATUS, user.getRelationshipStatus());
                    startActivityForResult(intent, REQUEST_RELATIONSHIP_STATUS);
                    break;
                case 2:
                    intent = new Intent(context, SetHeightActivity.class);
                    if (user.getHeight() != null)
                        intent.putExtra(Constants.HEIGHT, user.getHeight());
                    startActivityForResult(intent, REQUEST_HEIGHT);
                    break;
                case 3:
                    intent = new Intent(context, LookingForActivity.class);
                    if (user.getLookingFor() != null)
                        intent.putExtra(Constants.LOOKING_FOR, user.getLookingFor());
                    startActivityForResult(intent, REQUEST_LOOKING_FOR);
                    break;
                case 4:
                    intent = new Intent(context, DoYouDrinkActivity.class);
                    if (user.getDrink() != null)
                        intent.putExtra(Constants.DRINK, user.getDrink());
                    startActivityForResult(intent, REQUEST_DO_YOU_DRINK);
                    break;
                case 5:
                    intent = new Intent(context, DoYouSmokeActivity.class);
                    if (user.getSmoke() != null)
                        intent.putExtra(Constants.SMOKE, user.getSmoke());
                    startActivityForResult(intent, REQUEST_DO_YOU_SMOKE);
                    break;
                case 6:
                    intent = new Intent(context, DoYouUseCannabisActivity.class);
                    if (user.getCannabis() != null)
                        intent.putExtra(Constants.CANNABIS, user.getCannabis());
                    startActivityForResult(intent, REQUEST_CANNABIS);
                    break;
                case 7:
                    intent = new Intent(context, PoliticalViewsActivity.class);
                    if (user.getPoliticalViews() != null)
                        intent.putExtra(Constants.POLITICAL_VIEWS, user.getPoliticalViews());
                    startActivityForResult(intent, REQUEST_POLITICAL_VIEWS);
                    break;
                case 8:
                    intent = new Intent(context, YourReligionActivity.class);
                    if (user.getReligion() != null)
                        intent.putExtra(Constants.RELIGION, user.getReligion());
                    startActivityForResult(intent, REQUEST_RELIGION);
                    break;
                case 9:
                    intent = new Intent(context, YourDietActivity.class);
                    if (user.getDietLike() != null)
                        intent.putExtra(Constants.DIET_LIKE, user.getDietLike());
                    startActivityForResult(intent, REQUEST_DIET_LIKE);
                    break;
                case 10:
                    intent = new Intent(context, YourSignActivity.class);
                    if (user.getSign() != null)
                        intent.putExtra(Constants.SIGN, user.getSign());
                    startActivityForResult(intent, REQUEST_YOUR_SIGN);
                    break;
                case 11:
                    intent = new Intent(context, YourPetsActivity.class);
                    if (user.getPets() != null)
                        intent.putExtra(Constants.PETS, user.getPets());
                    startActivityForResult(intent, REQUEST_PETS);
                    break;
                case 12:
                    intent = new Intent(context, YourKidsActivity.class);
                    if (user.getKids() != null)
                        intent.putExtra(Constants.KIDS, user.getKids());
                    startActivityForResult(intent, REQUEST_KIDS);
                    break;
            }
        }
    }

    private void updateProfile() {
        customLoader.showIndicator();
        API api = RestAdapter.createAPI(context);
        Log.d(TAG, "loadCommunityPosts: " + sessionManager.getAccessToken());

        if (removeIdz.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < removeIdz.size(); i++) {

                String idz = removeIdz.get(i);

                stringBuilder.append(idz).append(",");

            }

            String removeIdz = stringBuilder.toString();
            if (removeIdz.endsWith(",")) {
                removeIdz = removeIdz.substring(0, removeIdz.length() - 1);
            }

            updateProfileParams.put("deleted_pics", removeIdz);
        }
        if (profileUpdated) {
            imagesList.add(SelectImage.prepareFilePart("profile_pic", profileUpdatedPath));
            callbackUpdateProfile = api.updateProfileWithImages(sessionManager.getAccessToken(), updateProfileParams, imagesList);
        } else {

            callbackUpdateProfile = api.updateProfile(sessionManager.getAccessToken(), updateProfileParams);
        }


        callbackUpdateProfile.enqueue(new Callback<CallbackUpdateProfile>() {
            @Override
            public void onResponse(Call<CallbackUpdateProfile> call, Response<CallbackUpdateProfile> response) {
                Log.d(TAG, "onResponse: " + response);
                responseUpdateProfile = response.body();
                if (responseUpdateProfile != null) {
                    if (responseUpdateProfile.getSuccess()) {
                        setResult(RESULT_OK);
                        setResultBack();
                    } else {
                        Log.d(TAG, "onResponse: " + responseUpdateProfile.getMessage());
                        customLoader.hideIndicator();
                        Toast.makeText(context, responseUpdateProfile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customLoader.hideIndicator();
                    ShowDialogues.SHOW_SERVER_ERROR_DIALOG(context);
                }
            }

            @Override
            public void onFailure(Call<CallbackUpdateProfile> call, Throwable t) {
                if (!call.isCanceled()) {
                    Log.d(TAG, "onResponse: " + t.getMessage());
                    customLoader.hideIndicator();
                }
            }
        });
    }


}
