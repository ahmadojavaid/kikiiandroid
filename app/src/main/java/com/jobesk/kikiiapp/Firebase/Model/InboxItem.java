package com.jobesk.kikiiapp.Firebase.Model;

import java.io.Serializable;

public class InboxItem implements Serializable {
    String count,name,profilePic,message,userId;
    long time;
    public InboxItem() { }


    public InboxItem(String count, String name, String profilePic, long time, String message, String userId) {
        this.count = count;
        this.name = name;
        this.profilePic = profilePic;
        this.time = time;
        this.message = message;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
