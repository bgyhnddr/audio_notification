import 'dart:async';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

class AudioNotification {
  static const MethodChannel _channel =
      const MethodChannel('audio_notification');

  static List<Function> queue = new List();

  static bool sending = false;

  static sendQueue() {
    if (queue.length > 0) {
      queue[0]().then((val) {
        queue.removeAt(0);
        sendQueue();
      });
    } else {
      sending = false;
    }
  }

  static send(Function exec) {
    if (sending) {
      queue.add(exec);
    } else {
      sending = true;
      exec().then((val) {
        sendQueue();
      });
    }
  }

  static show({@required title, @required content}) {
    final Map<String, dynamic> params = <String, dynamic>{
      'title': title,
      'content': content,
    };
    send(() {
      return _channel.invokeMethod('show', params);
    });
  }

  static setPlayState(isPlaying) {
    final Map<String, dynamic> params = <String, dynamic>{
      "isPlaying": isPlaying
    };
    send(() {
      return _channel.invokeMethod('setPlayState', params);
    });
  }

  static setContent(content) {
    final Map<String, dynamic> params = <String, dynamic>{
      'content': content,
    };
    send(() {
      return _channel.invokeMethod('setContent', params);
    });
  }

  static hide() {
    send(() {
      return _channel.invokeMethod('hide');
    });
  }

  static setMethodCallHandler(Function(String) callback) {
    _channel.setMethodCallHandler((MethodCall methodCall) async {
      callback(methodCall.method);
    });
  }
}
