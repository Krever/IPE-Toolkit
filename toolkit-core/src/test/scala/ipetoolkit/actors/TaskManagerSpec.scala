package ipetoolkit.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.messages._
import ipetoolkit.model.Task


class TaskManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {

  def this() = this(ActorSystem("TaskManagerSpec"))

  val busMock = mock[CentralEventBus]

  class TestTaskManager extends TaskManager()(busMock) {
    override def onTaskProgressUpdated(uid:String, progress:Double): Unit = super.onTaskProgressUpdated(uid, progress)
    override def onTaskCancelled(uid: String): Unit = super.onTaskCancelled(uid)
    override def onTaskCreated(task: Task): Unit = super.onTaskCreated(task)
    override def onTaskFinished(uid: String): Unit = super.onTaskFinished(uid)

    def cancelTaskPublic(uid: String) = super.cancelTask(uid)
  }


  "TaskManager" should {

    "subscribe to TaskManagement on start up" in {
      val tmActorRef = TestActorRef(new TestTaskManager)

      verify(busMock).subscribe(tmActorRef, classOf[TaskManagement])
    }

    "handle task create message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)

      tmActorRef ! NewTask(task, new Message {})

      verify(taskManager).onTaskCreated(task)
    }

    "handle task cancel message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      tmActorRef ! NewTask(task, new Message {})

      tmActorRef ! TaskCancelled(task.uid)

      verify(taskManager).onTaskCancelled(task.uid)
    }

    "cancel existing task on method call" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      taskManager.cancelTaskPublic(task.uid)

      verify(busMock).publish(cancellationMsg)
    }

    "throw IAException when cancelling not existing task" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)

      intercept[IllegalArgumentException] {
        taskManager.cancelTaskPublic(task.uid)
      }

    }


    "update task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val (uid, progress) = ("someUid", 0.0)

      tmActorRef ! TaskProgressUpdate(uid, progress)

      verify(taskManager).onTaskProgressUpdated(uid, progress)
    }

    "handle finished task" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val finishedTask = task.copy(progress = 1.0)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      tmActorRef ! TaskProgressUpdate(task.uid, finishedTask.progress)

      verify(taskManager).onTaskFinished(task.uid)
    }

    "be aware of other messages" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val msg = new Message {}

      tmActorRef ! msg

      verify(taskManager).unhandled(msg)
    }
  }

}
