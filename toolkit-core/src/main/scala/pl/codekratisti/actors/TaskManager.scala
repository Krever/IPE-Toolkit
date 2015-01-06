package pl.codekratisti.actors

import akka.actor.Actor
import akka.event.Logging
import pl.codekratisti.bus.CentralEventBus
import pl.codekratisti.messages._
import pl.codekratisti.model.Task

import scala.collection.mutable


/**
 * Controller of long running tasks in system.
 *
 * Reacts to following messages:
 * $ [[NewTask]] - Adds task and cancellation message to list
 * $ [[TaskProgressUpdate]] - Updates tasks progress. If progress is equal to 1, task is removed from list.
 * $ [[TaskCancelled]] - Removes task from list
 * $ [[CancelTask]] - Publishes cancellation message on event bus
 *
 * */
class TaskManager(eventBus :CentralEventBus) extends Actor{

  private val runningTasks: mutable.Map[String, (Task,Message)] = mutable.Map()
  private val log = Logging(context.system, this)


  override final def receive: Receive = {
    case tm :TaskManagement => tm match {
      case NewTask(task, cancellationMsg) =>
        log.info("Adding task: {}", task)
        runningTasks(task.uid) = (task, cancellationMsg)
        taskAdded(task)

      case TaskProgressUpdate(uid, progress) =>
        log.info("Updating task({}) with progress: {}", uid, progress)
        runningTasks.get(uid) match {
          case Some((task, msg)) =>
            val updatedTask = task.copy(progress = progress)
            taskUpdated(updatedTask)
            if(progress != 1.0)
              runningTasks.put(task.uid, (updatedTask, msg))
            else
              runningTasks.remove(task.uid)
          case None =>
            log.warning("Updating not existing task")
            //TODO publish warn message

        }


      case TaskCancelled(uid) =>
        log.info("Removing task with uid: {}", uid)
        runningTasks.remove(uid)
        taskRemoved(uid)

      case CancelTask(uid) => cancelTask(uid)
    }
  }

  override def unhandled(message: Any): Unit = {
    log.warning("Unhandled message: {}", message)
    super.unhandled(message)
  }

  protected final def cancelTask(uid:String) = {
    log.info("Cancelling task with uid: {}", uid)
    val (_, cancellationMsg) = runningTasks(uid)
    eventBus.publish(cancellationMsg)
  }

  protected final def getTasks :Map[String, Task]= runningTasks.map(e => e._1 -> e._2._1).toMap

  protected def taskUpdated(task:Task) :Unit= {}

  protected def taskRemoved(uid:String) :Unit= {}

  protected def taskAdded(task:Task) :Unit= {}


}
