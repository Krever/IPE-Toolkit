package ipetoolkit.task

import javafx.collections.FXCollections

import akka.actor._
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.Message
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.postfixOps


class TaskManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar {

  def this() = this(ActorSystem("TaskManagerSpec"))

  implicit val eventBus = mock[ClassBasedEventBusLike]

  "TaskManagerBase" should {

    "subscribe to TaskManagment on start" in {

      val taskList = FXCollections.observableArrayList[Task]()
      val actor = TestActorRef(TaskManager.props(taskList))

      verify(eventBus).subscribe(actor, classOf[TaskManagement])
    }

    "add started task to give list" in {
      val taskList = FXCollections.observableArrayList[Task]()
      val actor = TestActorRef(TaskManager.props(taskList))

      val taskA = Task("1", "A", None, 0.0)

      actor ! TaskStarted(taskA, null)

      taskList.contains(taskA) should be(true)

    }

    "update progress keeping order of tasks" in {

      val taskA = Task("1", "A", None, 0.0)
      val taskB = Task("2", "B", None, 0.0)
      val taskC = Task("3", "C", None, 0.0)

      val taskList = FXCollections.observableArrayList(taskA, taskB, taskC)
      val actor = TestActorRef(TaskManager.props(taskList))

      actor ! TaskProgressUpdate(taskB.uid, 0.5)

      taskList.size() should be(3)
      taskList.get(1) shouldEqual taskB.copy(progress = 0.5)
    }

    "mark task as 'cancelRequested' on CancelTask message" in {
      val taskList = FXCollections.observableArrayList[Task]()
      val actor = TestActorRef(TaskManager.props(taskList))

      val taskA = Task("1", "A", None, 0.0)
      actor ! TaskStarted(taskA, new Message {})

      actor ! CancelTask(taskA.uid)

      taskList.get(0) shouldEqual taskA.copy(cancelRequested = true)
    }

    "publish cancellation message when cancellation is requested" in {
      val taskList = FXCollections.observableArrayList[Task]()
      val actor = TestActorRef(TaskManager.props(taskList))

      val taskA = Task("1", "A", None, 0.0)
      case class StopMe(uid: String) extends Message
      val cancMsg = StopMe(taskA.uid)
      actor ! TaskStarted(taskA, cancMsg)

      actor ! CancelTask(taskA.uid)

      verify(eventBus).publish(cancMsg)
    }

    "remove task from list when stopped" in {
      val taskA = Task("1", "A", None, 0.0)

      val taskList = FXCollections.observableArrayList(taskA)
      val actor = TestActorRef(TaskManager.props(taskList))

      actor ! TaskStopped(taskA.uid)

      taskList.isEmpty should be(true)
    }

  }

}
