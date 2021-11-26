package com.jobesk.kikiiapp.Firebase;

import com.jobesk.kikiiapp.Firebase.Model.FirebaseUserModel;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by phamngocthanh on 7/29/17.
 */

public class AppState {
    public static FirebaseUser currentFireUser;
    public static FirebaseUserModel currentBpackCustomer;

    public static String email_address;
    public static String first_name;
    public static String last_name;
    public static String photo_url;
}
