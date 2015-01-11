package ipetoolkit.actors

import javafx.collections.ObservableList

import akka.actor.TypedProps
import akka.event.Logging
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.model.{Identifiable, Task}

import scala.collection.JavaConverters._

trait JFXTaskManager {
  def cancelTask(uid:String) : Unit
}

class JFXTaskManagerImpl protected (taskList: ObservableList[Task])(override implicit val eventBus:CentralEventBus) 
  extends TaskManager with JFXTaskManager {

  private val log = Logging(context.system, this)

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

  override protected def onTaskCancelled(uid: String): Unit = JFXTaskManagerImpl.removeTask(uid, taskList)

  override protected def onTaskFinished(uid: String): Unit = JFXTaskManagerImpl.removeTask(uid, taskList)

  def cancelTask(uid:String) = _cancelTask(uid)
}

object JFXTaskManagerImpl {

  def typedProps(taskList:ObservableList[Task])(implicit eventBus:CentralEventBus) :TypedProps[_ <: JFXTaskManager] = {
    TypedProps(classOf[JFXTaskManager],new JFXTaskManagerImpl(taskList)).withDispatcher("javafx-dispatcher")
  }


  private def removeTask(uid:String, list:ObservableList[Task]) :Boolean = {
    Identifiable.findIndex(uid, list.asScala) match {
      case Some(index) =>
        list.remove(index)
        true
      case None => false
    }
  }
}