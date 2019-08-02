package com.bgyhnddr.audio_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReturnSlot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "play":
                AudioNotificationPlugin.callEvent("play");
                break;
            case "pause":
                AudioNotificationPlugin.callEvent("pause");
                break;
        }
    }
}