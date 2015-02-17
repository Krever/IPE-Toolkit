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

  def this() = this(ActorSystem("TaskManagerSpec"))


  "TaskManagerBase" should {


    "handle task create message" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)

      tmActorRef ! TaskStarted(task, new Message {})

      expectMsg(OnTaskCreated(task))
    }

    "handle task cancel message" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      tmActorRef ! TaskStarted(task, new Message {})

      tmActorRef ! TaskCancelled(task.uid)

      expectMsgClass(classOf[OnTaskCreated])
      expectMsg(OnTaskCancelled(task.uid))
    }

    "cancel existing task on method call" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      val cancellationMsg = new Message {}
      tmActorRef ! TaskStarted(task, cancellationMsg)

      taskManager.cancelTask_Sync(task.uid)

      verify(busMock).publish(cancellationMsg)
    }

    "update task on message" in {
      val (taskManager, tmActorRef) = testObjects
      val (uid, progress) = ("someUid", 0.0)

      tmActorRef ! TaskProgressUpdate(uid, progress)

      expectMsgClass(classOf[OnTaskCreated])
      expectMsg(OnTaskProgressUpdated(uid, progress))
    }

    "handle finished task" in {
      val (taskManager, tmActorRef) = testObjects
      val task = Task("task1", "taskA", None, 0.0)
      val finishedTask = task.copy(progress = 1.0)
      val cancellationMsg = new Message {}
      tmActorRef ! TaskStarted(task, cancellationMsg)

      tmActorRef ! TaskProgressUpdate(task.uid, finishedTask.progress)

      expectMsgClass(classOf[OnTaskCreated])
      expectMsg(OnTaskFinished(task.uid))
    }
  }

  private def testObjects: (CancelTask_Sync, ActorRef) = {
    val typedActor = TypedActor(system).typedActorOf(
      TypedProps(classOf[CancelTask_Sync], new TestTaskManagerBase)
        .withDispatcher(CallingThreadDispatcher.Id)
    )
    val tmActorRef: ActorRef = TypedActor(system).getActorRefFor(typedActor)
    (typedActor, tmActorRef)
  }

  trait CancelTask_Sync extends TaskManager {
    def cancelTask_Sync(uid: String): Int = {
      cancelTask(uid)
      1
    }
  }

  case class OnTaskProgressUpdated(uid: String, progress: Double)

  case class OnTaskCancelled(uid: String)

  case class OnTaskCreated(task: Task)

  case class OnTaskFinished(uid: String)

  class TestTaskManagerBase extends TaskManagerBase with CancelTask_Sync {

    override val eventBus = busMock

    override def onTaskProgressUpdated(uid: String, progress: Double): Unit = testActor ! OnTaskProgressUpdated(uid, progress)

    override def onTaskCancelled(uid: String): Unit = testActor ! OnTaskCancelled(uid)

    override def onTaskCreated(task: Task): Unit = testActor ! OnTaskCreated(task)

    override def onTaskFinished(uid: String): Unit = testActor ! OnTaskFinished(uid)
  }

}
