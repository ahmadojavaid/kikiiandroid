package com.jobesk.kikiiapp.Events;

public class  CheckInternetEvent {
    boolean IS_INTERNET_AVAILABLE;

    public CheckInternetEvent(boolean IS_INTERNET_AVAILABLE) {
        this.IS_INTERNET_AVAILABLE = IS_INTERNET_AVAILABLE;
    }

    public boolean isIS_INTERNET_AVAILABLE() {
        return IS_INTERNET_AVAILABLE;
    }

    public void setIS_INTERNET_AVAILABLE(boolean IS_INTERNET_AVAILABLE) {
        this.IS_INTERNET_AVAILABLE = IS_INTERNET_AVAILABLE;
    }
}
