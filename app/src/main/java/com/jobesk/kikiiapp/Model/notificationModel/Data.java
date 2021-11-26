package com.jobesk.kikiiapp.Model.notificationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobesk.kikiiapp.Model.User;

/**
 * Created by Sheraz Ahmed on 1/19/2021.
 * sherazbhutta07@gmail.com
 */
public class Data {

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
