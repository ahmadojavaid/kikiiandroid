package com.jobesk.kikiiapp.Model.filters;

import java.util.List;

/**
 * Created by Sheraz Ahmed on 1/27/2021.
 * sherazbhutta07@gmail.com
 */
public class FilterModelShow {

    List<String> value;
    String title;
    String TitleUnchangeBle;
    boolean selected;


    public String getTitleUnchangeBle() {
        return TitleUnchangeBle;
    }

    public void setTitleUnchangeBle(String titleUnchangeBle) {
        TitleUnchangeBle = titleUnchangeBle;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
