
package com.jobesk.kikiiapp.Callbacks;

import com.jobesk.kikiiapp.Model.UserFbResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackFacebookLogin {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private UserFbResponse user;

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

    public UserFbResponse getUser() {
        return user;
    }

    public void setUser(UserFbResponse user) {
        this.user = user;
    }

}
