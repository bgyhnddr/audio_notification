import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class AudioNotification {
  static const MethodChannel _channel =
      const MethodChannel('audio_notification');

  static Future show({@required title, @required content}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'title': title,
      'content': content,
    };
    await _channel.invokeMethod('show', params);
  }

  static Future setPlayState(isPlaying) async {
    final Map<String, dynamic> params = <String, dynamic>{
      "isPlaying": isPlaying
    };
    await _channel.invokeMethod('setPlayState', params);
  }

  static Future setContent(content) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'content': content,
    };
    await _channel.invokeMethod('setContent', params);
  }

  static Future hide() async {
    await _channel.invokeMethod('hide');
  }

  static setMethodCallHandler(Function(bool) callback) {
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      callback(methodCall.method == "play");
    });
  }
}
