package com.jobesk.kikiiapp.Model.BlockedUserModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/18/2021.
 * sherazbhutta07@gmail.com
 */
public class BlockedUserModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("blocked_users")
    @Expose
    private List<BlockedUser> blockedUsers = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<BlockedUser> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(List<BlockedUser> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

}
