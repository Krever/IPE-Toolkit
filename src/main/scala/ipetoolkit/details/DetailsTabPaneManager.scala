package ipetoolkit.details

import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control.{Tab, TabPane}

import akka.actor.{Actor, ActorLogging, Props}
import ipetoolkit.bus.ClassBasedEventBus
import ipetoolkit.details.DetailsManagement.ShowDetails
import ipetoolkit.util.JavaFXDispatcher

import scala.collection.JavaConverters._


class DetailsTabPaneManager protected(tabPane: TabPane)(implicit eventBus: ClassBasedEventBus) extends Actor with ActorLogging {

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    eventBus.subscribe(self, classOf[DetailsManagement])
    tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS)
  }

  override def receive: Receive = {
    case ShowDetails(entry, content) =>
      val existingTabOpt = tabPane.getTabs.asScala.find(entry.uid == _.getId)
      existingTabOpt match {
        case Some(tab) =>
          tabPane.getSelectionModel.select(tab)
        case None =>
          val tab = new Tab(entry.nameProperty.get(), content)
          tab.setId(entry.uid)
          tab.textProperty().bindBidirectional(entry.nameProperty)
          tabPane.getTabs.add(tab)
          tabPane.getSelectionModel.select(tab)
      }
  }
}

object DetailsTabPaneManager {
  def props(tabPane: TabPane)(implicit eventBus: ClassBasedEventBus): Props = Props(new DetailsTabPaneManager(tabPane)).withDispatcher(JavaFXDispatcher.Id)
}