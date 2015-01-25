package ipetoolkit.sample

import akka.actor.ActorSystem
import ipetoolkit.bus.CentralEventBus


object Global {

  val actorSystem = ActorSystem("ipe-toolkit-sample")
  val centralEventBus = new CentralEventBus

}
