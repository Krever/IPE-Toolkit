package ipetoolkit.task

import javafx.collections.{FXCollections, ObservableList}

import akka.actor.{ActorSystem, TypedActor, TypedProps}
import akka.testkit.CallingThreadDispatcher
import ipetoolkit.bus.ClassBasedEventBus
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class JFXTaskManagerSpec extends WordSpec with Matchers with MockitoSugar {

  private implicit val eventBus = new ClassBasedEventBus
  private implicit val actorSystem = ActorSystem("JFXTaskManagerSpec")

  "JFXTaskManager" should {
    "add created task to list" in {
      val tasks = FXCollections.observableArrayList[Task]()
      val taskManager = testObject(tasks)
      val newTask = Task("task1","task1", None, 0.0)

      taskManager.onTaskCreatedP(newTask)

      tasks.size() should be (1)
      tasks.contains(newTask) should be (true)
    }

    "update task on list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskProgressUpdatedP(task.uid, 0.4)

      tasks.size() should be (1)
      tasks.get(0).progress should be (0.4)
    }

    "remove cancelled task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskCancelledP(task.uid)

      tasks.size() should be (0)
      tasks.contains(task) should be (false)
    }

    "remove finished task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskFinishedP(task.uid)

      tasks.size() should be (0)
      tasks.contains(task) should be (false)
    }

    "provide props for actor creation" in {
      val observableList = FXCollections.observableArrayList[Task]()
      val actor = TypedActor(actorSystem).typedActorOf(JFXTaskManager.typedProps(observableList))
    }

  }

  private def testObject(taskList: ObservableList[Task]): CallbacksPublisher = {
    TypedActor(actorSystem).typedActorOf(
      TypedProps(classOf[CallbacksPublisher], new TestJFXTaskManagerImpl(taskList))
        .withDispatcher(CallingThreadDispatcher.Id)
    )

  }

  trait CallbacksPublisher extends JFXTaskManager {
    val onTaskCreatedP: (Task) => Unit = onTaskCreated
    val onTaskProgressUpdatedP: (String, Double) => Unit = onTaskProgressUpdated
    val onTaskCancelledP: (String) => Unit = onTaskCancelled
    val onTaskFinishedP: (String) => Unit = onTaskFinished
  }

  // Test classes to publish protected methods
  class TestJFXTaskManagerImpl(taskList: ObservableList[Task]) extends JFXTaskManager(taskList) with CallbacksPublisher {

  }

}
