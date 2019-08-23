package com.bgyhnddr.audio_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.view.KeyEvent;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * AudioNotificationPlugin
 */
public class AudioNotificationPlugin implements MethodCallHandler {
    private static Registrar registrar;
    private static MethodChannel channel;
    private static MediaSession mMediaSession;
    public final static String PLAY = "com.bgyhnddr.audio_notification.PLAY";
    public final static String PAUSE = "com.bgyhnddr.audio_notification.PAUSE";

    /**
     * Plugin registration.
     */

    private AudioNotificationPlugin(Registrar r) {
        registrar = r;
    }

    public static void registerWith(Registrar registrar) {
        channel = new MethodChannel(registrar.messenger(), "audio_notification");
        channel.setMethodCallHandler(new AudioNotificationPlugin(registrar));

        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAY);
        filter.addAction(PAUSE);
        registrar.context().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AudioNotificationPlugin.callEvent("toggle");
            }
        }, filter);

        IntentFilter HEADSET_PLUG = new IntentFilter();
        HEADSET_PLUG.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registrar.context().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AudioNotificationPlugin.callEvent("noisy");
            }
        }, HEADSET_PLUG);
    }

    private static void initMediaSession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mMediaSession == null) {
                mMediaSession = new MediaSession(registrar.context(), "audio_notification_tag");
                mMediaSession.setCallback(new MediaSession.Callback() {
                    @Override
                    public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                        String intentAction = mediaButtonIntent.getAction();
                        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                            KeyEvent event = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                            if (event != null) {
                                int action = event.getAction();
                                if (action == KeyEvent.ACTION_UP) {
                                    AudioNotificationPlugin.callEvent("toggle");
                                }
                            }
                        }
                        return true;
                    }
                });

                mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

                if (!mMediaSession.isActive()) {
                    mMediaSession.setActive(true);
                }
            }
        }
    }

    private static void releaseMediaSession() {
        if (mMediaSession != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaSession.setActive(false);
            }
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        String title;
        String content;
        boolean isPlaying;
        switch (call.method) {
            case "show":
                title = call.argument("title");
                content = call.argument("content");
                StatusNotification.notify(registrar, title, content);
                initMediaSession();
                result.success(true);
                break;
            case "setPlayState":
                isPlaying = call.argument("isPlaying");
                StatusNotification.setPlayState(registrar, isPlaying);
                result.success(true);
                break;
            case "setContent":
                content = call.argument("content");
                StatusNotification.setContent(registrar, content);
                result.success(true);
                break;
            case "hide":
                StatusNotification.cancel(registrar.context());
                releaseMediaSession();
                result.success(true);
                break;
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            default:
                result.notImplemented();
        }
    }


    public static void callEvent(String event) {
        AudioNotificationPlugin.channel.invokeMethod(event, null, new Result() {
            @Override
            public void success(Object o) {
                // this will be called with o = "some string"
            }

            @Override
            public void error(String s, String s1, Object o) {
            }

            @Override
            public void notImplemented() {
            }
        });
    }
}
