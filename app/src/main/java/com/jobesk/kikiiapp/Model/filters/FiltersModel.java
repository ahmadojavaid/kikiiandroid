package com.jobesk.kikiiapp.Model.filters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/27/2021.
 * sherazbhutta07@gmail.com
 */
public class FiltersModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("filters")
    @Expose
    private List<Filter> filters = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

}
