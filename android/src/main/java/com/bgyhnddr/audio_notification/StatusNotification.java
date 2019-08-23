package com.bgyhnddr.audio_notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Helper class for showing and canceling status
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class StatusNotification {
    private static final String CHANNEL_ID = "audio_notification";
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Status";

    private static NotificationCompat.Builder builder;
    private static String _title;
    private static String _content;
    private static boolean _isPlaying = false;
    private static Notification notification;

    private static void notify(final Registrar registrar) {
        MediaStyle mediaStyle = new MediaStyle();
        mediaStyle.setShowActionsInCompactView(0);


        Intent toggleIntent = new Intent(_isPlaying ? AudioNotificationPlugin.PLAY : AudioNotificationPlugin.PAUSE);
        PendingIntent pendingToggleIntent = PendingIntent.getBroadcast(registrar.context(), 1, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notifyIntent = new Intent(registrar.context(), registrar.activity().getClass());
        notifyIntent.setAction(Intent.ACTION_MAIN);
        notifyIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                registrar.context(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder = new NotificationCompat.Builder(registrar.context(), CHANNEL_ID)
                .setVibrate(null)
                .setSound(null)
                .setLights(0, 0, 0)
                .setShowWhen(false)
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.ic_flusic)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(_title)
                .setContentText(_content)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(notifyPendingIntent)
                .addAction(_isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, "control", pendingToggleIntent);
        notification = builder.build();
        notify(registrar.context(), notification);
    }

    public static void notify(final Registrar registrar,
                              final String title, final String content) {
        _title = title;
        _content = content;
        notify(registrar);
    }

    public static void setPlayState(final Registrar registrar, final boolean isPlaying) {
        _isPlaying = isPlaying;
        notify(registrar);
    }

    public static void setContent(final Registrar registrar, final String content) {
        _content = content;
        notify(registrar);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription("Media playback controls");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 1, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 1);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
