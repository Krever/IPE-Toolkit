package ipetoolkit.task

import akka.actor.TypedActor.{PreStart, Receiver}
import akka.actor.{ActorRef, TypedActor}
import akka.event.Logging
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.util.Message

import scala.collection.mutable

trait TaskManager {
  def cancelTask(uid: String): Unit
}

/**
 * Controller of long running tasks in system.
 *
 * Reacts to following messages:
 * $ [[NewTask]] - [[TaskManagerBase#onTaskCreated]] is called
 * $ [[TaskProgressUpdate]] - [[TaskManagerBase#onTaskProgressUpdated]] is called if progress != 0 else [[TaskManagerBase#onTaskFinished]] is called
 * $ [[TaskCancelled]] - [[TaskManagerBase#onTaskCancelled]] is called
 *
 * */
class TaskManagerBase(implicit val eventBus: CentralEventBus) extends TaskManager with Receiver with PreStart {

  private lazy val log = Logging(TypedActor.context.system, TypedActor.context.self)
  private val cancellationMessages: mutable.Map[String, Message] = mutable.Map()

  override def preStart(): Unit = eventBus.subscribe(TypedActor.context.self, classOf[TaskManagement])

  override def onReceive(message: Any, sender: ActorRef): Unit = message match {
    case tm :TaskManagement => tm match {
      case NewTask(task, cancellationMsg) =>
        log.info("New task: {}", task)
        cancellationMessages.put(task.uid,cancellationMsg)
        onTaskCreated(task)

      case TaskProgressUpdate(uid, progress) =>
        log.info("Updating task({}) with progress: {}", uid, progress)
        if(progress == 1.0){
          cancellationMessages.remove(uid)
          onTaskFinished(uid)
        }
        else
          onTaskProgressUpdated(uid, progress)

      case TaskCancelled(uid) =>
        log.info("Removing task with uid: {}", uid)
        cancellationMessages.remove(uid)
        onTaskCancelled(uid)
    }
  }

  protected def onTaskCreated(task:Task) :Unit= {}

  protected def onTaskProgressUpdated(uid:String, progress:Double) :Unit= {}

  protected def onTaskCancelled(uid:String) :Unit= {}

  protected def onTaskFinished(uid:String) :Unit= {}

  override def cancelTask(uid: String) = {
    log.info("Cancelling task with uid: {}", uid)
    cancellationMessages.get(uid) match {
      case Some(cancellationMsg) => eventBus.publish(cancellationMsg)
      case None =>
        log.warning("Cancellation message not found for uid: {}", uid)
    }
  }
}
