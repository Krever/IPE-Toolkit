package ipetoolkit.task

import ipetoolkit.util.{Manager, Message}

import scala.collection.mutable

trait TaskManager {
  def cancelTask(uid: String): Unit
}

/**
 * Controller of long running tasks in system.
 *
 * Reacts to following messages:
 * $ [[TaskStarted]] - [[TaskManagerBase#onTaskCreated]] is called
 * $ [[TaskProgressUpdate]] - [[TaskManagerBase#onTaskProgressUpdated]] is called if progress != 0 else [[TaskManagerBase#onTaskFinished]] is called
 * $ [[TaskCancelled]] - [[TaskManagerBase#onTaskCancelled]] is called
 *
 **/
class TaskManagerBase(protected val onTaskCreated: (Task) => Unit = _ => {},
                      protected val onTaskProgressUpdated: (String, Double) => Unit = (_, _) => {},
                      protected val onTaskCancelled: (String) => Unit = _ => {},
                      protected val onTaskFinished: (String) => Unit = _ => {})
  extends Manager[TaskManagement] with TaskManager {

  private val cancellationMessages: mutable.Map[String, Message] = mutable.Map()

  override def manage(managementMessage: TaskManagement): Unit = managementMessage match {
    case TaskStarted(task, cancellationMsg) =>
      log.info("New task: {}", task)
      cancellationMessages.put(task.uid, cancellationMsg)
      onTaskCreated(task)

    case TaskProgressUpdate(uid, progress) =>
      log.info("Updating task({}) with progress: {}", uid, progress)
      if (progress == 1.0) {
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

  override def cancelTask(uid: String) = {
    log.info("Cancelling task with uid: {}", uid)
    cancellationMessages.get(uid) match {
      case Some(cancellationMsg) => eventBus.publish(cancellationMsg)
      case None =>
        log.warning("Cancellation message not found for uid: {}", uid)
    }
  }
}
