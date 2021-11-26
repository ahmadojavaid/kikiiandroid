package com.jobesk.kikiiapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseError;
import com.jobesk.kikiiapp.Firebase.ChangeEventListener;
import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.jobesk.kikiiapp.Firebase.Services.UserService;
import com.jobesk.kikiiapp.Model.ProfileUser;
import com.jobesk.kikiiapp.Netwrok.Constants;
import com.jobesk.kikiiapp.R;
import com.jobesk.kikiiapp.Utils.SessionManager;
import com.squareup.picasso.Picasso;

public class MatchDoneActivity extends AppCompatActivity {


    ImageView imageSender, imageReceiver;

    private FirebaseUserModel user = null;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_done);


        Intent intent = getIntent();
        String otherImage = intent.getStringExtra("otherUserImage");
        String otherId = intent.getStringExtra("otherUserID");


        imageSender = findViewById(R.id.imageSender);
        imageReceiver = findViewById(R.id.imageReceiver);


        SessionManager sessionManager = new SessionManager(getApplicationContext());


        ProfileUser profile = sessionManager.getProfileUser();
        String profileImage = profile.getProfilePic();

        Picasso.get().load(profileImage).fit().centerCrop().into(imageSender);

        Picasso.get().load(otherImage).fit().centerCrop().into(imageReceiver);


        userService = new UserService();
        userService.setOnChangedListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(EventType type, int index, int oldIndex) {

            }

            @Override
            public void onDataChanged() {
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });


        Button id_start_chat = findViewById(R.id.id_start_chat);
        id_start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                user = userService.getUserIdById(otherId);


                if (user != null) {

                    Intent intent = new Intent(MatchDoneActivity.this, SingleMessagingActivity.class);
                    intent.putExtra(Constants.ID, user.getUserId());
                    intent.putExtra(Constants.START_NAME, user.getUserName());
                    intent.putExtra(Constants.IMAGE, user.getImage());
                    intent.putExtra(Constants.CREATE_RIDE_OBJ, user);
                    startActivity(intent);
                    finish();
                }


            }
        });


    }


}