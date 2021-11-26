
package com.jobesk.kikiiapp.Callbacks;

import com.jobesk.kikiiapp.Model.PostComment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackAddComment {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("comment")
    @Expose
    private PostComment comment;

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

    public PostComment getComment() {
        return comment;
    }

    public void setComment(PostComment comment) {
        this.comment = comment;
    }

}
