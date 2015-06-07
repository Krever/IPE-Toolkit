package ipetoolkit.bus

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import ipetoolkit.util.Message
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class CentralEventBusSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CentralEventBusSpec"))

  case class MsgOne(txt: String) extends Message
  class MsgTwo(txt: String) extends Message
  case class MsgTwoTwo(txt1: String) extends MsgTwo(txt1)

  "CentralEventBus" should {

    "deliver subscribed message type" in {
      val bus = new ClassBasedEventBusLike {}
      bus.subscribe(testActor, classOf[MsgOne])
      val msg = MsgOne("test")
      bus.publish(msg)
      expectMsg(msg)
    }

    "not deliver not subscribed message type" in {
      val bus = new ClassBasedEventBusLike {}
      bus.subscribe(testActor, classOf[MsgOne])
      val msg = MsgTwoTwo("test")
      bus.publish(msg)
      expectNoMsg()
    }

    "deliver subclass of subscribed message type" in {
      val bus = new ClassBasedEventBusLike {}
      bus.subscribe(testActor, classOf[MsgTwo])
      val msg = MsgTwoTwo("test")
      bus.publish(msg)
      expectMsg(msg)
    }


  }

}
