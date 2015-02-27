package ipetoolkit.task

import javafx.collections.ObservableList

import akka.actor.TypedProps
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.JavaFXDispatcher

import scala.collection.JavaConverters._


class JFXTaskManager protected(taskList: ObservableList[Task])(override implicit val eventBus: ClassBasedEventBusLike)
  extends TaskManagerBase {

  override protected val onTaskCreated: (Task) => Unit = task => taskList.add(task)

  override protected val onTaskProgressUpdated: (String, Double) => Unit = (uid, progress) => {
    taskList.asScala.zipWithIndex.find(_._1.uid == uid).map(_._2) match {
      case Some(taskIndex) =>
        val task = taskList.get(taskIndex)
        taskList.remove(taskIndex)
        taskList.add(taskIndex, task.copy(progress = progress))
      case None => log.warning("Task with uid({}) not found", uid)
    }
  }

  override protected val onTaskCancelled: (String) => Unit = uid => taskList.asScala.filter(_.uid == uid).foreach(taskList.remove(_)) //JFXTaskManager.removeTask(uid, taskList)

  override protected val onTaskFinished: (String) => Unit = uid => taskList.asScala.filter(_.uid == uid).foreach(taskList.remove(_))
}

object JFXTaskManager {

  def typedProps(taskList: ObservableList[Task])(implicit eventBus: ClassBasedEventBusLike): TypedProps[_ <: TaskManager] = {
    TypedProps(classOf[TaskManager], new JFXTaskManager(taskList)).withDispatcher(JavaFXDispatcher.Id)
  }

}