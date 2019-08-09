import 'package:flutter/material.dart';
import 'package:audio_notification/audio_notification.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool _isPlaying = false;
  @override
  void initState() {
    AudioNotification.setMethodCallHandler((method) {
      setState(() {
        _isPlaying = !_isPlaying;
      });
    });
    super.initState();
  }

  Future test() async {
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(true);
    await AudioNotification.setPlayState(false);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Container(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              FlatButton(
                child: Text('Show notification'),
                onPressed: () => AudioNotification.show(
                    title: "test", content: 'testcontent'),
              ),
              !_isPlaying
                  ? FlatButton(
                      child: Text('play'),
                      onPressed: () {
                        setState(() {
                          _isPlaying = true;
                        });
                        AudioNotification.setPlayState(true);
                      },
                    )
                  : FlatButton(
                      child: Text('pause'),
                      onPressed: () {
                        setState(() {
                          _isPlaying = false;
                        });
                        AudioNotification.setPlayState(false);
                      },
                    ),
              FlatButton(
                child: Text('set Content'),
                onPressed: () => AudioNotification.setContent(
                    DateTime.now().millisecondsSinceEpoch.toString()),
              ),
              FlatButton(
                child: Text('Hide notification'),
                onPressed: () => AudioNotification.hide(),
              ),
              FlatButton(
                child: Text('fast notification test'),
                onPressed: () {
                  setState(() {
                    AudioNotification.setPlayState(true);
                  });
                  setState(() {
                    AudioNotification.setPlayState(true);
                  });
                  setState(() {
                    AudioNotification.setPlayState(false);
                  });
                },
              ),
              Text('Status: ' + (_isPlaying ? 'playing' : 'pausing'))
            ],
          ),
        )),
      ),
    );
  }
}
