import Flutter
import UIKit

public class SwiftFlutterLalaSharePlugin: NSObject, FlutterPlugin {

    private var result: FlutterResult?

  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_lala_share", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterLalaSharePlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {

      if (call.method == "getPlatformVersion") {
          result("iOS " + UIDevice.current.systemVersion)
      } else if ("share" == call.method) {
          self.result = result
          result(share(call: call))
      } else {
          result(FlutterMethodNotImplemented)
      }

    result("iOS " + UIDevice.current.systemVersion)
  }

  public func share(call: FlutterMethodCall) -> Bool {
      let args = call.arguments as? [String: Any?]

      let emailSubject = args!["emailSubject"] as? String
      let textMessage = args!["textMessage"] as? String
      let urlToShare = args!["urlToShare"] as? String

      if (emailSubject == nil || emailSubject!.isEmpty) {
          return false
      }

      var sharedItems : Array<NSObject> = Array()
      var textList : Array<String> = Array()

      if (urlToShare != nil && urlToShare != "") {
          textList.append(urlToShare!)
      }

      if (textMessage != nil && textMessage != "") {
          textList.append(textMessage!)
      }

      var textToShare = ""

      if (!textList.isEmpty) {
          textToShare = textList.joined(separator: "\n\n")
      }

      sharedItems.append((textToShare as NSObject?)!)

      let activityViewController = UIActivityViewController(activityItems: sharedItems, applicationActivities: nil)

      if (emailSubject != nil && emailSubject != "") {
          activityViewController.setValue(emailSubject, forKeyPath: "subject");
      }

      DispatchQueue.main.async {
          UIApplication.topViewController()?.present(activityViewController, animated: true, completion: nil)
      }

      return true
  }

}

extension UIApplication {
    class func topViewController(controller: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController? {
        if let navigationController = controller as? UINavigationController {
            return topViewController(controller: navigationController.visibleViewController)
        }
        if let tabController = controller as? UITabBarController {
            if let selected = tabController.selectedViewController {
                return topViewController(controller: selected)
            }
        }
        if let presented = controller?.presentedViewController {
            return topViewController(controller: presented)
        }
        return controller
    }
}
