package ipetoolkit.workspace

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, WordSpecLike}


class WorkspaceManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with Matchers {

  def this() = this(ActorSystem("TaskManagerSpec"))

  //TODO

  "WorkspaceManager" should {
    "subscribe to WorkspaceManagament on preStart" in {

    }

    "add WorkspaceEntry to given parent" in {

    }

    "add WorkspaceEntry to root if no parent is given" in {

    }

    "set WorkspaceEntry as root if one is not set" in {

    }

    "remove WorkspaceEntry with given uid" in {

    }

    "create new workspace" in {

    }

    "load given workspace" in {

    }

    "save given workspace" in {

    }

  }

}