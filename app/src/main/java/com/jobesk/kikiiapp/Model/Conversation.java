
package com.jobesk.kikiiapp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conversation {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("participant_1_id")
    @Expose
    private Integer participant1Id;
    @SerializedName("participant_2_id")
    @Expose
    private Integer participant2Id;
    @SerializedName("deleted_by_user_id")
    @Expose
    private Object deletedByUserId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("participant_1")
    @Expose
    private Participant1 participant1;
    @SerializedName("participant_2")
    @Expose
    private Participant2 participant2;
    @SerializedName("messages")
    @Expose
    private List<LastMessage> messages = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParticipant1Id() {
        return participant1Id;
    }

    public void setParticipant1Id(Integer participant1Id) {
        this.participant1Id = participant1Id;
    }

    public Integer getParticipant2Id() {
        return participant2Id;
    }

    public void setParticipant2Id(Integer participant2Id) {
        this.participant2Id = participant2Id;
    }

    public Object getDeletedByUserId() {
        return deletedByUserId;
    }

    public void setDeletedByUserId(Object deletedByUserId) {
        this.deletedByUserId = deletedByUserId;
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

    public Participant1 getParticipant1() {
        return participant1;
    }

    public void setParticipant1(Participant1 participant1) {
        this.participant1 = participant1;
    }

    public Participant2 getParticipant2() {
        return participant2;
    }

    public void setParticipant2(Participant2 participant2) {
        this.participant2 = participant2;
    }

    public List<LastMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<LastMessage> messages) {
        this.messages = messages;
    }

}
