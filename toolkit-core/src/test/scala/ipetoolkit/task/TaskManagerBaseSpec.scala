package ipetoolkit.task

import akka.actor._
import akka.testkit.{CallingThreadDispatcher, ImplicitSender, TestKit}
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.Message
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps


class TaskManagerBaseSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {

  val busMock = mock[ClassBasedEventBusLike]
  val onTaskCreated: (Task) => Unit = mock[(Task) => Unit]
  val onTaskProgressUpdated: (String, Double) => Unit = mock[(String, Double) => Unit]
  val onTaskCancelled: (String) => Unit = mock[(String) => Unit]
  val onTaskFinished: (String) => Unit = mock[(String) => Unit]

  def this() = this(ActorSystem("TaskManagerSpec"))

  val callTimeout = timeout(1000)
  
  
  "TaskManagerBase" should {


    "handle task create message" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)

      tmActorRef ! TaskStarted(task, new Message {})

      verify(onTaskCreated, callTimeout).apply(task)
    }

    "handle task cancel message" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      tmActorRef ! TaskStarted(task, new Message {})

      tmActorRef ! TaskCancelled(task.uid)

      verify(onTaskCancelled, callTimeout).apply(task.uid)
    }

    "cancel existing task on method call" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      val cancellationMsg = new Message {}
      tmActorRef ! TaskStarted(task, cancellationMsg)

      taskManager.cancelTask(task.uid)

      verify(busMock, callTimeout).publish(cancellationMsg)
    }

    "update task on message" in {
      val (taskManager, tmActorRef) = testObjects
      val (uid, progress) = ("someUid", 0.0)

      tmActorRef ! TaskProgressUpdate(uid, progress)

      verify(onTaskProgressUpdated, callTimeout).apply(uid, progress)
    }

    "handle finished task" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      val finishedTask = task.copy(progress = 1.0)
      val cancellationMsg = new Message {}
      tmActorRef ! TaskStarted(task, cancellationMsg)

      tmActorRef ! TaskProgressUpdate(task.uid, finishedTask.progress)

      verify(onTaskFinished, callTimeout)
    }
  }

  private def testObjects: (TaskManager, ActorRef) = {
    val typedActor = TypedActor(system).typedActorOf(
      TypedProps(classOf[TaskManager], new TestTaskManagerBase)
        .withDispatcher(CallingThreadDispatcher.Id)
    )
    val tmActorRef: ActorRef = TypedActor(system).getActorRefFor(typedActor)
    (typedActor, tmActorRef)
  }

  class TestTaskManagerBase extends TaskManagerBase(onTaskCreated, onTaskProgressUpdated, onTaskCancelled, onTaskFinished) {
    override val eventBus = busMock

    override def cancelTask(uid: String) = super.cancelTask(uid)
  }

}
