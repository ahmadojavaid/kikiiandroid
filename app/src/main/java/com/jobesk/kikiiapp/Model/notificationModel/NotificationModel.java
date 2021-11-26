package com.jobesk.kikiiapp.Model.notificationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/19/2021.
 * sherazbhutta07@gmail.com
 */
public class NotificationModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("notifications")
    @Expose
    private List<Notification> notifications = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }


}
