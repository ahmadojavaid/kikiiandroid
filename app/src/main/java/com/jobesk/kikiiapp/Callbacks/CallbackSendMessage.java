
package com.jobesk.kikiiapp.Callbacks;

import com.jobesk.kikiiapp.Model.Message;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackSendMessage {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Message data;

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

    public Message getData() {
        return data;
    }

    public void setData(Message data) {
        this.data = data;
    }

}
