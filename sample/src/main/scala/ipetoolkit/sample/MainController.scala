package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TabPane

import ipetoolkit.bus.IPEEventBus
import ipetoolkit.details.{DetailsController, DetailsTabPaneManager}
import ipetoolkit.workspace.WorkspaceEntry
import ipetoolkit.workspace.WorkspaceManagement.{LoadWorkspace, NewWorkspace, SaveWorkspace}

class MainController extends Initializable with DetailsController {

  lazy val system = Global.actorSystem
  implicit val eEventBus = IPEEventBus

  val workspaceDir = "/tmp/ipetoolkit-test/" //TODO zaminic na popup albo sciezke do user.home; propagowac do zmiennej globalnej

  @FXML
  var detailsTabPane: TabPane = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    system.actorOf(DetailsTabPaneManager.props(detailsTabPane))
  }


  def newWorkspace() = {
    IPEEventBus.publish(NewWorkspace(workspaceDir, new BasicWorkspaceEntryView(new BasicWorkspaceEntry("Root"))))
  }

  def saveWorkspace() = {
    IPEEventBus.publish(SaveWorkspace(workspaceDir))
  }

  def loadWorkspace() = {
    IPEEventBus.publish(LoadWorkspace(workspaceDir, (model: WorkspaceEntry) => new BasicWorkspaceEntryView(model)))
  }

  override def setModel(workspaceEntry: WorkspaceEntry): Unit = {

  }
}
