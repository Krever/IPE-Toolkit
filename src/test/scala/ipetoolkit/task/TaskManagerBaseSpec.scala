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

  def this() = this(ActorSystem("TaskManagerSpec"))

  "TaskManagerBase" should {


    "handle task create message" in {
      val actor = _system.actorOf(Props(new TaskManager()))

      actor ! TaskStarted


    }

    "handle task cancel message" in {
    }

    "cancel existing task on method call" in {
    }

    "update task on message" in {
    }

    "handle finished task" in {
    }
  }

}
