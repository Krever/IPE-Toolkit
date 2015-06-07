package ipetoolkit.task

import javafx.collections.ObservableList

import akka.actor.{Props, Actor}
import akka.event.EventBus


class TaskManager(taskList: ObservableList[Task])(implicit val eventBus: EventBus) extends Actor {

  override def receive: Receive = {
    case _ => ???
  }

}

object TaskManager {
  def props(taskList: ObservableList[Task]) = Props(new TaskManager(taskList))
}
