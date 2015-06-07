package ipetoolkit.util

import akka.actor.TypedActor.{PreStart, Receiver}
import akka.actor.{ActorRef, TypedActor}
import akka.event.{Logging, LoggingAdapter}
import ipetoolkit.bus.{ClassBasedEventBusLike, IPEEventBus}

import scala.reflect.{ClassTag, classTag}

abstract class Manager[ManagementMessagesType <: Message : ClassTag] extends Receiver with PreStart {

  def eventBus: ClassBasedEventBusLike = IPEEventBus

  override def preStart(): Unit = eventBus.subscribe(TypedActor.context.self, classTag[ManagementMessagesType].runtimeClass)

  protected lazy val log: LoggingAdapter = Logging(TypedActor.context.system, TypedActor.context.self)

  override def onReceive(message: Any, sender: ActorRef) = {
    message match {
      case managementMessage: ManagementMessagesType => manage(managementMessage)
    }
  }

  def manage(managementMessage: ManagementMessagesType)
}
