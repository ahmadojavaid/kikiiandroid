package com.jobesk.kikiiapp.Firebase.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by phamngocthanh on 7/19/17.
 */

@IgnoreExtraProperties
public class MediaModel implements Serializable {
    String path,type;

    public MediaModel() {
    }

    public MediaModel(String path, String type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
