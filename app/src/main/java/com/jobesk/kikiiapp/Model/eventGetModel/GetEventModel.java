package com.jobesk.kikiiapp.Model.eventGetModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobesk.kikiiapp.Model.Event;

/**
 * Created by Sheraz Ahmed on 1/15/2021.
 * sherazbhutta07@gmail.com
 */
public class GetEventModel {



    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("event")
    @Expose
    private Event event;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
