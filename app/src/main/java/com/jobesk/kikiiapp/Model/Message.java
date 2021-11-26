
package com.jobesk.kikiiapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("sender_id")
    @Expose
    private SenderId senderId;
    @SerializedName("receiver_id")
    @Expose
    private ReceiverId receiverId;
    @SerializedName("conversation_id")
    @Expose
    private Integer conversationId;
    @SerializedName("read_at")
    @Expose
    private Object readAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public SenderId getSenderId() {
        return senderId;
    }

    public void setSenderId(SenderId senderId) {
        this.senderId = senderId;
    }

    public ReceiverId getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(ReceiverId receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getConversationId() {
        return conversationId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Object getReadAt() {
        return readAt;
    }

    public void setReadAt(Object readAt) {
        this.readAt = readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
