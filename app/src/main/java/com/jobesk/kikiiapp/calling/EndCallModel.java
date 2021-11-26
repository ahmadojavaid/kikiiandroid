package com.jobesk.kikiiapp.calling;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EndCallModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("caller_user_id")
    @Expose
    private Integer callerUserId;
    @SerializedName("caller_user_name")
    @Expose
    private String callerUserName;
    @SerializedName("caller_profile_pic")
    @Expose
    private String callerProfilePic;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCallerUserId() {
        return callerUserId;
    }

    public void setCallerUserId(Integer callerUserId) {
        this.callerUserId = callerUserId;
    }

    public String getCallerUserName() {
        return callerUserName;
    }

    public void setCallerUserName(String callerUserName) {
        this.callerUserName = callerUserName;
    }

    public String getCallerProfilePic() {
        return callerProfilePic;
    }

    public void setCallerProfilePic(String callerProfilePic) {
        this.callerProfilePic = callerProfilePic;
    }

}
