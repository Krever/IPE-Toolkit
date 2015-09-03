package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TabPane

import ipetoolkit.bus.IPEEventBus
import ipetoolkit.details.DetailsTabPaneManager
import ipetoolkit.workspace.WorkspaceManagement.NewWorkspace

class MainController extends Initializable {

  lazy val system = Global.actorSystem
  implicit val eEventBus = IPEEventBus

  val workspaceDir = "/tmp/ipetoolkit-test/" //TODO zaminic na popup albo sciezke do user.home

  @FXML
  var detailsTabPane: TabPane = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    system.actorOf(DetailsTabPaneManager.props(detailsTabPane))
  }

  def addWorkspaceEntry() = {
//    IPEEventBus.publish(AddWorkspaceEntry(BasicWorkspaceEntry(), None))
  }

  def newWorkspace() = {
    IPEEventBus.publish(NewWorkspace(workspaceDir, BasicWorkspaceEntry("Root")))
  }

  def saveWorkspace() = {
//    IPEEventBus.publish(SaveWorkspace())
  }

  def loadWorkspace() = {
//    IPEEventBus.publish(LoadWorkspace(workspaceDir, BasicWorkspaceEntryDeserializer))
  }

}