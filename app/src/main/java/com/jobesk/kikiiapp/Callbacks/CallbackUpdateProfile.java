
package com.jobesk.kikiiapp.Callbacks;

import com.jobesk.kikiiapp.Model.CompleteUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackUpdateProfile {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private CompleteUser user;

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
    public CompleteUser getUser() {
        return user;
    }

    public void setUser(CompleteUser user) {
        this.user = user;
    }
}
