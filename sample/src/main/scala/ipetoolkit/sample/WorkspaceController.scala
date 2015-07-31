package ipetoolkit.sample

import java.net.URL
import java.util.{ResourceBundle, UUID}
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._

import akka.actor.ActorRef
import ipetoolkit.bus.{ClassBasedEventBusLike, IPEEventBus}
import ipetoolkit.util.Message
import ipetoolkit.workspace.{AddWorkspaceEntry, RemoveWorkspaceEntry, WorkspaceEntry, WorkspaceManager}


class WorkspaceController extends Initializable {

  @FXML
  var workspaceTreeView: TreeView[WorkspaceEntry] = _

  implicit val actorSystem = Global.actorSystem

  var workspaceManager: ActorRef = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    implicit val eventBus: ClassBasedEventBusLike = IPEEventBus
    workspaceTreeView.setRoot(new BasicWorkspaceEntry().treeItem)
    workspaceManager = actorSystem.actorOf(WorkspaceManager.props(workspaceTreeView))
  }

}

case class BasicWorkspaceEntry(uid: String = UUID.randomUUID().toString, detailsOpener: Option[Message] = None)(implicit eventBus: ClassBasedEventBusLike) extends WorkspaceEntry {
  override val treeItem: TreeItem[WorkspaceEntry] = new TreeItem[WorkspaceEntry](this)

  override def contextMenu: Option[ContextMenu] = {
    val item1 = new MenuItem("Add")
    item1.setOnAction(new EventHandler[ActionEvent]() {
      def handle(e: ActionEvent) = {
        eventBus.publish(AddWorkspaceEntry(BasicWorkspaceEntry(), Some(uid)))
      }
    })
    val item2 = new MenuItem("Remove")
    item2.setOnAction(new EventHandler[ActionEvent]() {
      def handle(e: ActionEvent) = {
        eventBus.publish(RemoveWorkspaceEntry(uid))
      }
    })
    Some(new ContextMenu(item1, item2))
  }
}