
package com.jobesk.kikiiapp.Callbacks;

import java.util.List;

import com.jobesk.kikiiapp.Model.FellowUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackGetMyFriends {

   /* @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;*/
    @SerializedName("next_offset")
    @Expose
    private Integer nextOffset;
    @SerializedName("friends")
    @Expose
    private List<FellowUser> fellowUsers = null;

  /*  public Boolean getSuccess() {
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
    }*/

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public List<FellowUser> getFellowUsers() {
        return fellowUsers;
    }

    public void setFellowUsers(List<FellowUser> fellowUsers) {
        this.fellowUsers = fellowUsers;
    }

}
