package app.lalalife.flutter_lala_share;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterLalaSharePlugin */
public class FlutterLalaSharePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private FlutterPluginBinding flutterPluginBinding;

//  private Registrar _registrar;

//  private FlutterLalaSharePlugin(Registrar mRegistrar) {
//    this._registrar = mRegistrar;
//  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.flutterPluginBinding = flutterPluginBinding;
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_lala_share");
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_lala_share");
    channel.setMethodCallHandler(new FlutterLalaSharePlugin());
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("share")) {
      _share(call, result);
    }else {
      result.notImplemented();
    }
  }

  private void _share(MethodCall call, Result result) {
    Log.d("Flutter_Android", "_share function");
    try
    {
      String emailSubject = call.argument("emailSubject");
      String textMessage = call.argument("textMessage");
      String urlToShare = call.argument("urlToShare");

      if (emailSubject == null || emailSubject.isEmpty())
      {
        Log.println(Log.ERROR, "", "FlutterShare Error: Email Subject null or empty");
        result.error("FlutterLalaShare: Email Subject cannot be null or empty", null, null);
        return;
      }

      ArrayList<String> extraTextList = new ArrayList<>();

      if (urlToShare != null && !urlToShare.isEmpty()) {
        extraTextList.add(urlToShare);
      }

      if (textMessage != null && !textMessage.isEmpty()) {
        extraTextList.add(textMessage);
      }


      String extraText = "";

      if (!extraTextList.isEmpty()) {
        extraText = TextUtils.join("\n\n", extraTextList);
      }

      Intent intent = new Intent();
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setAction(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
      intent.putExtra(Intent.EXTRA_TEXT, extraText);

      Intent chooserIntent = Intent.createChooser(intent, "Select");
      chooserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      flutterPluginBinding.getApplicationContext().startActivity(chooserIntent);

      result.success(true);
    }
    catch (Exception ex)
    {
      Log.println(Log.ERROR, "", "FlutterLalaShare: Error");
      result.error(ex.getMessage(), null, null);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
