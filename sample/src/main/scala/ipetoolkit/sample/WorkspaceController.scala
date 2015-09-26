package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._

import akka.actor.ActorRef
import ipetoolkit.bus.{ClassBasedEventBus, IPEEventBus}
import ipetoolkit.workspace._


class WorkspaceController extends Initializable with DetailsController {

  @FXML
  var workspaceTreeView: TreeView[WorkspaceEntryView] = _

  implicit val actorSystem = Global.actorSystem

  var workspaceManager: ActorRef = _

  var model: WorkspaceEntry = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    implicit val eventBus: ClassBasedEventBus = IPEEventBus
    workspaceManager = actorSystem.actorOf(WorkspaceManager.props(workspaceTreeView))
  }

  override def setModel(workspaceEntry: WorkspaceEntry): Unit = {
    this.model = workspaceEntry
  }

}

