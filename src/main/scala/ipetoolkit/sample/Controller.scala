package ipetoolkit.sample

import java.util.UUID
import java.util.concurrent.TimeUnit

import ipetoolkit.messages.{NewTask, TaskCancelled, TaskProgressUpdate}
import ipetoolkit.model.Task

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class Controller {

  lazy val eventBus = Global.centralEventBus
  lazy val system = Global.actorSystem

  def someAction() = {
    val uid = UUID.randomUUID().toString.substring(0, 10)
    val task = Task(uid, "name", None, 0.0)
    val cancellationMsg = TaskCancelled(task.uid)
    eventBus.publish(NewTask(task, cancellationMsg))

    //simulate task running
    import scala.concurrent.ExecutionContext.Implicits.global
    system.scheduler.scheduleOnce(Duration.create(2, TimeUnit.SECONDS)) {
      eventBus.publish(TaskProgressUpdate(uid, 0.3))
    }
    system.scheduler.scheduleOnce(Duration.create(4, TimeUnit.SECONDS)) {
      eventBus.publish(TaskProgressUpdate(uid, 0.7))
    }
    system.scheduler.scheduleOnce(Duration.create(6, TimeUnit.SECONDS)) {
      eventBus.publish(TaskProgressUpdate(uid, 1.0))
    }
  }

}

