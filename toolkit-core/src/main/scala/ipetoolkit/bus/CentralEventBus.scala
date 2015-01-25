package ipetoolkit.bus

import akka.actor.ActorRef
import akka.event.{EventBus, SubchannelClassification}
import akka.util.Subclassification
import ipetoolkit.messages.Message

/**
 * Event bus used across the system
 */
class CentralEventBus extends EventBus with SubchannelClassification  {
  override type Event = Message
  override type Classifier = Class[_]
  override type Subscriber = ActorRef

  override protected def publish(event: Event, subscriber: Subscriber): Unit = subscriber ! event

  override protected def classify(event: Event): Classifier = event.getClass

  override protected implicit def subclassification: Subclassification[Classifier] = new ClassSubclassification
}

private class ClassSubclassification extends Subclassification[Class[_]] {
  override def isEqual(x: Class[_], y: Class[_]): Boolean =
    x.equals(y)

  override def isSubclass(x: Class[_], y: Class[_]): Boolean =
    y.isAssignableFrom(x)
}
