package ipetoolkit.actors

import akka.actor.Actor
import akka.event.Logging
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.messages._
import ipetoolkit.model.Task

import scala.collection.mutable


/**
 * Controller of long running tasks in system.
 *
 * Reacts to following messages:
 * $ [[NewTask]] - [[TaskManager#onTaskCreated]] is called
 * $ [[TaskProgressUpdate]] - [[TaskManager#onTaskProgressUpdated]] is called if progress != 0 else [[TaskManager#onTaskFinished]] is called
 * $ [[TaskCancelled]] - [[TaskManager#onTaskCancelled]] is called
 *
 * */
class TaskManager(implicit val eventBus :CentralEventBus) extends Actor{

  private val cancellationMessages: mutable.Map[String, Message] = mutable.Map()
  private val log = Logging(context.system, this)


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = eventBus.subscribe(self, classOf[TaskManagement])

  override final def receive: Receive = {
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

  override def unhandled(message: Any): Unit = {
    log.warning("Unhandled message: {}", message)
    super.unhandled(message)
  }

  @throws[IllegalArgumentException]
  protected final def cancelTask(uid:String) = {
    log.info("Cancelling task with uid: {}", uid)
    cancellationMessages.get(uid) match {
      case Some(cancellationMsg) => eventBus.publish(cancellationMsg)
      case None =>
        log.warning("Cancellation message not found for uid: {}", uid)
        throw new IllegalArgumentException("cancellation message not found")
    }
  }

  protected def onTaskCreated(task:Task) :Unit= {}

  protected def onTaskProgressUpdated(uid:String, progress:Double) :Unit= {}

  protected def onTaskCancelled(uid:String) :Unit= {}

  protected def onTaskFinished(uid:String) :Unit= {}
}
