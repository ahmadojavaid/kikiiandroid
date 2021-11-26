
package com.jobesk.kikiiapp.Callbacks;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobesk.kikiiapp.Model.UserModelOTP;

public class CallbackSentOTP {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private UserModelOTP user;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserModelOTP getUser() {
        return user;
    }

    public void setUser(UserModelOTP user) {
        this.user = user;
    }

}
