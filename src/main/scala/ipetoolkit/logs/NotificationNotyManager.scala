package ipetoolkit.logs

import org.controlsfx.control.Notifications

/**
 * Created by krever on 7/11/15.
 */
class NotificationNotyManager(show: ((Notifications) => Unit) = NotificationNotyManager.defaultShow) {

}

object NotificationNotyManager {

  val defaultShow = (notification: Notifications) => notification.show()

}