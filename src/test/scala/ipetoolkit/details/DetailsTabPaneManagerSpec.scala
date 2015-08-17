package ipetoolkit.details

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import ipetoolkit.bus.ClassBasedEventBus
import org.mockito.Mockito
import org.scalatest._

/**
 * Created by krever on 8/17/15.
 */
class DetailsTabPaneManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("DetailsTabPaneManagerSpec"))

  implicit val eventBusMock = Mockito.mock(classOf[ClassBasedEventBus])

  "DetailsTabPaneManager" should {
    "subscribe to DetailsManagement on start" in {
      val manager = TestActorRef(new DetailsTabPaneManager(null) {})
      Mockito.verify(eventBusMock).subscribe(manager, classOf[DetailsManagement])
    }

    "add Tab to TabPane on ShowDetails message" in {
      /*
      THIS CANNOT BE TESTED DUE TO CONSTRAINTS IN MOCKING CREATING JAVAFX CONTROLS

      //given
      val tabPaneSpy = Mockito.spy(new TabPane())
      val tabsSpy = Mockito.spy(tabPaneSpy.getTabs)
      val (title, node) = ("title", new Text("text"))
      //when
      val manager = TestActorRef(new DetailsTabPaneManager(tabPaneSpy) {})
      manager ! ShowDetails(title, node)
      //then
      Mockito.verify(tabsSpy).add(new Tab(title, node))*/
    }
  }

}