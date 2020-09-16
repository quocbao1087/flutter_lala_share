import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class FlutterLalaShare {
  static const MethodChannel _channel =
      const MethodChannel('flutter_lala_share');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> share(
      {@required String emailSubject,
        String textMessage,
        String urlToShare}) async {
    assert(emailSubject != null && emailSubject.isNotEmpty);

    if (emailSubject == null || emailSubject.isEmpty) {
      throw FlutterError('Email Subject cannot be null');
    }

    final bool success = await _channel.invokeMethod('share', <String, dynamic>{
      'emailSubject': emailSubject,
      'textMessage': textMessage,
      'urlToShare': urlToShare,
    });

    return success;
  }
}
