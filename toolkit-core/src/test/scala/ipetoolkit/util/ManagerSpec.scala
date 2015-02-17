package ipetoolkit.util

import akka.actor.{ActorRef, ActorSystem, TypedActor, TypedProps}
import akka.testkit.{CallingThreadDispatcher, TestKit}
import ipetoolkit.bus.ClassBasedEventBus
import org.mockito.Mockito._
import org.scalatest.{Matchers, WordSpecLike}

class ManagerSpec(_system: ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers {

  def this() = this(ActorSystem("ManagerSpec"))

  case class TestMessage() extends Message

  case class ManageCalled(msg: Message)

  val eventBusSpy = spy(new ClassBasedEventBus)

  class TestManager extends Manager[TestMessage] {
    override val eventBus = eventBusSpy

    override def manage(managementMessage: TestMessage): Unit = {
      testActor ! ManageCalled(managementMessage)
    }
  }

  "A Manager" should {
    "subscribe to given message type" in {

      val (testManager, actorRef) = testObjects

      verify(eventBusSpy, timeout(10000)).subscribe(actorRef, classOf[TestMessage])
    }

    "call manage on proper message" in {
      val (_, actorRef) = testObjects
      val msg = TestMessage()

      actorRef ! msg

      expectMsg(ManageCalled(msg))
    }

    "not call manage on inproper message" in {
      val (_, actorRef) = testObjects

      actorRef ! new Message {}

      expectNoMsg()
    }

  }

  def testObjects: (AnyRef, ActorRef) = {
    val testManager = TypedActor(system).typedActorOf(TypedProps(classOf[AnyRef], new TestManager).withDispatcher(CallingThreadDispatcher.Id))
    val actorRef = TypedActor(system).getActorRefFor(testManager)
    (testManager, actorRef)
  }
}
