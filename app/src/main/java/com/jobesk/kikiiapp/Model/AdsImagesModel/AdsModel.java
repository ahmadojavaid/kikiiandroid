package com.jobesk.kikiiapp.Model.AdsImagesModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/13/2021.
 * sherazbhutta07@gmail.com
 */
public class AdsModel {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}
