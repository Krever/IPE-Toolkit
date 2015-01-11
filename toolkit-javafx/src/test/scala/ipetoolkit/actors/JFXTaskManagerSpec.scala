package ipetoolkit.actors

import javafx.collections.{FXCollections, ObservableList}

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import ipetoolkit.bus.CentralEventBus
import ipetoolkit.model.Task
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class JFXTaskManagerSpec extends WordSpec with Matchers with MockitoSugar {

  // Test class to publish protected methods
  class TestJFXTaskManagerImpl(taskList:ObservableList[Task]) extends JFXTaskManagerImpl(taskList) {
    override def onTaskCreated(task: Task): Unit = super.onTaskCreated(task)
    override def onTaskProgressUpdated(uid: String, progress: Double): Unit = super.onTaskProgressUpdated(uid, progress)
    override def onTaskCancelled(uid: String): Unit = super.onTaskCancelled(uid)
    override def onTaskFinished(uid: String): Unit = super.onTaskFinished(uid)
  }

  private implicit val eventBus = mock[CentralEventBus]
  private implicit val actorSystem = ActorSystem("JFXTaskManagerSpec")

  "JFXTaskManager" should {
    "add created task to list" in {
      val tasks = FXCollections.observableArrayList[Task]()
      val actorRef = TestActorRef(new TestJFXTaskManagerImpl(tasks))
      val actor = actorRef.underlyingActor
      val newTask = Task("task1","task1", None, 0.0)

      actor.onTaskCreated(newTask)

      tasks.size() should be (1)
      tasks.contains(newTask) should be (true)
    }

    "update task on list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val actorRef = TestActorRef(new TestJFXTaskManagerImpl(tasks))
      val actor = actorRef.underlyingActor

      actor.onTaskProgressUpdated(task.uid, 0.4)

      tasks.size() should be (1)
      tasks.get(0).progress should be (0.4)
    }

    "remove cancelled task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val actorRef = TestActorRef(new TestJFXTaskManagerImpl(tasks))
      val actor = actorRef.underlyingActor

      actor.onTaskCancelled(task.uid)

      tasks.size() should be (0)
      tasks.contains(task) should be (false)
    }

    "remove finished task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val actorRef = TestActorRef(new TestJFXTaskManagerImpl(tasks))
      val actor = actorRef.underlyingActor

      actor.onTaskFinished(task.uid)

      tasks.size() should be (0)
      tasks.contains(task) should be (false)
    }

  }

}
