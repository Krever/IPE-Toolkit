package ipetoolkit.details

import javafx.scene.control.{Tab, TabPane}

import akka.actor.{Actor, ActorLogging, Props}
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.details.DetailsManagement.ShowDetails
import ipetoolkit.util.JavaFXDispatcher


class DetailsTabPaneManager protected(tabPane: TabPane)(implicit eventBus: ClassBasedEventBusLike) extends Actor with ActorLogging {

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    eventBus.subscribe(self, classOf[DetailsManagement])
  }

  override def receive: Receive = {
    case ShowDetails(title, content) =>
      //TODO only focus if tab already exists
      tabPane.getTabs.add(new Tab(title, content))
  }
}

object DetailsTabPaneManager {
  def props(tabPane: TabPane)(implicit eventBus: ClassBasedEventBusLike): Props = Props(new DetailsTabPaneManager(tabPane)).withDispatcher(JavaFXDispatcher.Id)
}