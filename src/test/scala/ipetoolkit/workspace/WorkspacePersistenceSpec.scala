package ipetoolkit.workspace

import java.io.File
import javax.xml.bind.annotation.XmlRootElement

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import ipetoolkit.workspace.WorkspacePersistence.{Load, Loaded, Persist}
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.io.Source
import scala.util.Success

/**
 * Created by krever on 9/22/15.
 */

class TestWorkspaceEntryView(val model: WorkspaceEntry) extends WorkspaceEntryView {
  override def childrenToViews: PartialFunction[WorkspaceEntry, WorkspaceEntryView] = {
    case x: TestWorkspaceEntry => new TestWorkspaceEntryView(x)
    case x: Test2WorkspaceEntry => new TestWorkspaceEntryView(x)
  }
}

@XmlRootElement
class TestWorkspaceEntry extends WorkspaceEntry {
  setName("TestWorkspaceEntry")
}

@XmlRootElement
class Test2WorkspaceEntry extends WorkspaceEntry {
  setName("Test2WorkspaceEntry")
}


class WorkspacePersistenceSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with Matchers {

  def this() = this(ActorSystem("WorkspacePersistenceSpec"))

  "WorkspacePersistence" should {
    "marshall and unmarshall given workspace entry" in {
      val persister = TestActorRef(Props[WorkspacePersistence])

      val file = File.createTempFile("WorkspacePersistenceSpec", ".xml")

      val rootEntry = new TestWorkspaceEntry
      rootEntry.addChild(new TestWorkspaceEntry)
      rootEntry.addChild(new Test2WorkspaceEntry)

      persister ! Persist(rootEntry, file)

      Source.fromFile(file).getLines().foreach(println)

      implicit val timeout = Timeout(1 second)
      val Success(Loaded(Success(root))) = (persister ? Load(file)).value.get

      root.uuid shouldEqual rootEntry.uuid
      root.children.size shouldEqual rootEntry.children.size
      root.children(0).uuid shouldEqual rootEntry.children(0).uuid
      root.children(1).uuid shouldEqual rootEntry.children(1).uuid
    }

  }

}
