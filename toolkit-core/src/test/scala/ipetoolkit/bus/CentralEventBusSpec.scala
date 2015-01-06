package ipetoolkit.bus

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ipetoolkit.messages.Message


class CentralEventBusSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CentralEventBusSpec"))

  case class MsgOne(txt: String) extends Message
  class MsgTwo(txt: String) extends Message
  case class MsgTwoTwo(txt1: String) extends MsgTwo(txt1)

  "CentralEventBus" should {

    "deliver subscribed message type" in {
      val bus = new CentralEventBus
      bus.subscribe(testActor, classOf[MsgOne])
      val msg = MsgOne("test")
      bus.publish(msg)
      expectMsg(msg)
    }

    "not deliver not subscribed message type" in {
      val bus = new CentralEventBus
      bus.subscribe(testActor, classOf[MsgOne])
      val msg = MsgTwoTwo("test")
      bus.publish(msg)
      expectNoMsg()
    }

    "deliver subclass of subscribed message type" in {
      val bus = new CentralEventBus
      bus.subscribe(testActor, classOf[MsgTwo])
      val msg = MsgTwoTwo("test")
      bus.publish(msg)
      expectMsg(msg)
    }


  }

}
