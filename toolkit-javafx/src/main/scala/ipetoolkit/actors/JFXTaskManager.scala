package ipetoolkit.actors

import javafx.collections.ObservableList

import akka.actor.{TypedActor, TypedProps}
import akka.event.Logging
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.model.{Identifiable, Task}
import ipetoolkit.util.JavaFXDispatcher

import scala.collection.JavaConverters._


class JFXTaskManager protected(taskList: ObservableList[Task])(override implicit val eventBus: CentralEventBus)
  extends TaskManagerBase with TaskManager {

  private lazy val log = Logging(TypedActor.context.system, TypedActor.context.self)

  override protected def onTaskCreated(task: Task): Unit = {
    taskList.add(task)
  }

  override protected def onTaskProgressUpdated(uid: String, progress: Double): Unit = {
    Identifiable.findIndex(uid, taskList.asScala) match {
      case Some(taskIndex) =>
        val task = taskList.get(taskIndex)
        taskList.remove(taskIndex)
        taskList.add(taskIndex, task.copy(progress = progress))
      case None => log.warning("Task with uid({}) not found", uid)
    }
  }

  override protected def onTaskCancelled(uid: String): Unit = JFXTaskManager.removeTask(uid, taskList)

  override protected def onTaskFinished(uid: String): Unit = JFXTaskManager.removeTask(uid, taskList)
}

object JFXTaskManager {

  def typedProps(taskList: ObservableList[Task])(implicit eventBus: CentralEventBus): TypedProps[_ <: TaskManager] = {
    TypedProps(classOf[TaskManager], new JFXTaskManager(taskList)).withDispatcher(JavaFXDispatcher.Id)
  }


  private def removeTask(uid: String, list: ObservableList[Task]): Boolean = {
    Identifiable.findIndex(uid, list.asScala) match {
      case Some(index) =>
        list.remove(index)
        true
      case None => false
    }
  }
}