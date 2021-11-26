
package com.jobesk.kikiiapp.Callbacks;

import java.util.List;

import com.jobesk.kikiiapp.Model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackGetOnlineUsers {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("users")
    @Expose
    private List<User> onlineUsers = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<User> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<User> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
}
