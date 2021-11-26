
package com.jobesk.kikiiapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastMessage {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("conversation_id")
    @Expose
    private Integer conversationId;
    @SerializedName("read_at")
    @Expose
    private Object readAt;
    @SerializedName("media")
    @Expose
    private Object media;

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

    public Object getMedia() {
        return media;
    }

    public void setMedia(Object media) {
        this.media = media;
    }

}
