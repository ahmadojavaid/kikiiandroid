package com.jobesk.kikiiapp.Events;

import com.google.firebase.messaging.RemoteMessage;

public class NotificationReceivedEvent {
    boolean IS_NOTIFY;
    RemoteMessage remoteMessage;

    public NotificationReceivedEvent(boolean IS_NOTIFY, RemoteMessage remoteMessage) {
        this.IS_NOTIFY=IS_NOTIFY;
        this.remoteMessage=remoteMessage;
    }

    public boolean isIS_NOTIFY() {
        return IS_NOTIFY;
    }

    public void setIS_NOTIFY(boolean IS_NOTIFY) {
        this.IS_NOTIFY = IS_NOTIFY;
    }

    public RemoteMessage getRemoteMessage() {
        return remoteMessage;
    }

    public void setRemoteMessage(RemoteMessage remoteMessage) {
        this.remoteMessage = remoteMessage;
    }
}
