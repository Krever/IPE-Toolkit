package ipetoolkit.task

import javafx.collections.{FXCollections, ObservableList}

import akka.actor.{TypedActor, TypedProps}
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

      taskManager.onTaskCreated_Sync(newTask)

      tasks.size() should be (1)
      tasks.contains(newTask) should be (true)
    }

    "update task on list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskProgressUpdated_Sync(task.uid, 0.4)

      tasks.size() should be (1)
      tasks.get(0).progress should be (0.4)
    }

    "remove cancelled task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskCancelled_Sync(task.uid)

      tasks.size() should be (0)
      tasks.contains(task) should be (false)
    }

    "remove finished task from list" in {
      val task = Task("task1","task1", None, 0.0)
      val tasks = FXCollections.observableArrayList[Task](task)
      val taskManager = testObject(tasks)

      taskManager.onTaskFinished_Sync(task.uid)

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

  trait CallbacksPublisher {
    def onTaskCreated_Sync(task: Task): Int

    def onTaskProgressUpdated_Sync(uid: String, progress: Double): Int

    def onTaskCancelled_Sync(uid: String): Int

    def onTaskFinished_Sync(uid: String): Int
  }

  // Test classes to publish protected methods
  class TestJFXTaskManagerImpl(taskList: ObservableList[Task]) extends JFXTaskManager(taskList) with CallbacksPublisher {
    override def onTaskCreated_Sync(task: Task) = {
      super.onTaskCreated(task); 1
    }

    override def onTaskProgressUpdated_Sync(uid: String, progress: Double) = {
      super.onTaskProgressUpdated(uid, progress); 1
    }

    override def onTaskCancelled_Sync(uid: String) = {
      super.onTaskCancelled(uid); 1
    }

    override def onTaskFinished_Sync(uid: String) = {
      super.onTaskFinished(uid); 1
    }
  }

}
