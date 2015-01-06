package pl.codekratisti.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import pl.codekratisti.bus.CentralEventBus
import pl.codekratisti.messages._
import pl.codekratisti.model.Task


class TaskManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {

  def this() = this(ActorSystem("TaskManagerSpec"))

  val busMock = mock[CentralEventBus]

  class TestTaskManager extends TaskManager(busMock) {
    override def taskUpdated(task: Task): Unit = super.taskUpdated(task)

    override def taskRemoved(uid: String): Unit = super.taskRemoved(uid)

    override def taskAdded(task: Task): Unit = super.taskAdded(task)

    def getTasksPublic = super.getTasks
    def cancelTaskPublic(uid: String) = super.cancelTask(uid)
    override def unhandled(any: Any) = super.unhandled(any)

  }


  "TaskManager" should {
    "create task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)

      tmActorRef ! NewTask(task, new Message {})

      verify(taskManager).taskAdded(task)
      val tasks = taskManager.getTasksPublic
      tasks.size should be(1)
      tasks.get(task.uid) should be(Some(task))
    }

    "remove task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      tmActorRef ! NewTask(task, new Message {})

      tmActorRef ! TaskCancelled(task.uid)

      verify(taskManager).taskRemoved(task.uid)
      val tasks = taskManager.getTasksPublic
      tasks.size should be(0)
    }

    "cancel task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      tmActorRef ! CancelTask(task.uid)

      verify(busMock).publish(cancellationMsg)
    }

    "cancel task on call" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      taskManager.cancelTaskPublic(task.uid)

      verify(busMock).publish(cancellationMsg)
    }

    "update existing task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)

      tmActorRef ! TaskProgressUpdate("someUid", 0.0)
      //TODO add assertions
    }

    "handle update of not existing task on message" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val updatedTask = task.copy(progress = 0.9)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      tmActorRef ! TaskProgressUpdate(task.uid, updatedTask.progress)

      val tasks = taskManager.getTasksPublic
      tasks.size should be(1)
      tasks.get(task.uid) should be( Some(updatedTask))
    }

    "remove finished task" in {
      val taskManager = spy(TestActorRef(new TestTaskManager).underlyingActor)
      val tmActorRef = TestActorRef(taskManager)
      val task = Task("task1", "taskA", None, 0.0)
      val finishedTask = task.copy(progress = 1.0)
      val cancellationMsg = new Message {}
      tmActorRef ! NewTask(task, cancellationMsg)

      tmActorRef ! TaskProgressUpdate(task.uid, finishedTask.progress)

      taskManager.getTasksPublic.size should be(0)
      verify(taskManager).taskUpdated(finishedTask)
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
