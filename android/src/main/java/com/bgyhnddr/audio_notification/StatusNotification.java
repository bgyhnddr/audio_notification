package com.bgyhnddr.audio_notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

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

    private static void notify(final Context context) {
        MediaStyle mediaStyle = new MediaStyle();
        mediaStyle.setShowActionsInCompactView(0);


        Intent nextIntent = new Intent(context, NotificationReturnSlot.class)
                .setAction(_isPlaying ? "pause" : "play");
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
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
                .addAction(_isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, "1232131", pendingNextIntent);
        notification = builder.build();
        notify(context, notification);
    }

    public static void notify(final Context context,
                              final String title, final String content) {
        _title = title;
        _content = content;
        notify(context);
    }

    public static void setPlayState(final Context context, final boolean isPlaying) {
        _isPlaying = isPlaying;
        notify(context);
    }

    public static void setContent(final Context context, final String content) {
        _content = content;
        notify(context);
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


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        MediaSessionCallback() {
            super();
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            super.onMediaButtonEvent(mediaButtonEvent);
            //接收到监听事件
            return true;

        }

    }
}
