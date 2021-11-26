
package com.jobesk.kikiiapp.Callbacks;

import com.jobesk.kikiiapp.Model.ValueChips;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallbackGetCategoryChip {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("value")
    @Expose
    private ValueChips value;
    @SerializedName("IsChecked")
    @Expose
    private String isChecked;

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

    public ValueChips getValue() {
        return value;
    }

    public void setValue(ValueChips value) {
        this.value = value;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

}
