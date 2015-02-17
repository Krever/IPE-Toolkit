package ipetoolkit.task

import javafx.collections.ObservableList

import akka.actor.TypedProps
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.{Identifiable, JavaFXDispatcher}

import scala.collection.JavaConverters._


class JFXTaskManager protected(taskList: ObservableList[Task])(override implicit val eventBus: ClassBasedEventBusLike)
  extends TaskManagerBase with TaskManager {

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

  override protected def onTaskCancelled(uid: String): Unit = taskList.asScala.filter(_.uid == uid).foreach(taskList.remove(_)) //JFXTaskManager.removeTask(uid, taskList)

  override protected def onTaskFinished(uid: String): Unit = taskList.asScala.filter(_.uid == uid).foreach(taskList.remove(_))
}

object JFXTaskManager {

  def typedProps(taskList: ObservableList[Task])(implicit eventBus: ClassBasedEventBusLike): TypedProps[_ <: TaskManager] = {
    TypedProps(classOf[TaskManager], new JFXTaskManager(taskList)).withDispatcher(JavaFXDispatcher.Id)
  }

}