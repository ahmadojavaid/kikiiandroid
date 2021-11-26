package com.jobesk.kikiiapp.Model;

import com.plumillonforge.android.chipview.Chip;

public class CuriosityChipModel implements Chip {
    private String mName;
    private int position;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CuriosityChipModel(String name,int position) {
        mName = name;
        this.position=position;
    }

    @Override
    public String getText() {
        return mName;
    }
}
