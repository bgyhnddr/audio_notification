package com.bgyhnddr.audio_notification;

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

    /**
     * Plugin registration.
     */

    private AudioNotificationPlugin(Registrar r) {
        registrar = r;
    }

    public static void registerWith(Registrar registrar) {
        channel = new MethodChannel(registrar.messenger(), "audio_notification");
        channel.setMethodCallHandler(new AudioNotificationPlugin(registrar));
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
                StatusNotification.notify(registrar.context(), title, content);
                result.success(true);
                break;
            case "setPlayState":
                isPlaying = call.argument("isPlaying");
                StatusNotification.setPlayState(registrar.context(), isPlaying);
                result.success(true);
                break;
            case "setContent":
                content = call.argument("content");
                StatusNotification.setContent(registrar.context(), content);
                result.success(true);
                break;
            case "hide":
                StatusNotification.cancel(registrar.context());
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
            public void error(String s, String s1, Object o) {}

            @Override
            public void notImplemented() {}
        });
    }
}
