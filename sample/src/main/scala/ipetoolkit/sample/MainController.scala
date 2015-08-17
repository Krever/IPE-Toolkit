package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TabPane

import ipetoolkit.bus.IPEEventBus
import ipetoolkit.details.DetailsTabPaneManager
import ipetoolkit.workspace._

class MainController extends Initializable {

  lazy val system = Global.actorSystem
  implicit val eEventBus = IPEEventBus

  val workspaceDir = "/tmp/ipetoolkit-test/" //TODO zaminic na popup albo sciezke do user.home

  @FXML
  var detailsTabPane: TabPane = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    system.actorOf(DetailsTabPaneManager.props(detailsTabPane))
  }

  /*  def someAction() = {
      val uid = UUID.randomUUID().toString.substring(0, 10)
      val task = Task(uid, "name", None, 0.0)
      val cancellationMsg = TaskStopped(task.uid)
      IPEEventBus.publish(TaskStarted(task, cancellationMsg))

      //simulate task running
      import scala.concurrent.ExecutionContext.Implicits.global
      system.scheduler.scheduleOnce(Duration.create(2, TimeUnit.SECONDS)) {
        IPEEventBus.publish(TaskProgressUpdate(uid, 0.3))
      }
      system.scheduler.scheduleOnce(Duration.create(4, TimeUnit.SECONDS)) {
        IPEEventBus.publish(TaskProgressUpdate(uid, 0.7))
      }
      system.scheduler.scheduleOnce(Duration.create(6, TimeUnit.SECONDS)) {
        IPEEventBus.publish(TaskProgressUpdate(uid, 1.0))
      }
    }*/

  def addWorkspaceEntry() = {
    IPEEventBus.publish(AddWorkspaceEntry(BasicWorkspaceEntry(), None))
  }

  def newWorkspace() = {
    IPEEventBus.publish(NewWorkspace(workspaceDir, BasicWorkspaceEntry("Root")))
  }

  def saveWorkspace() = {
    IPEEventBus.publish(SaveWorkspace())
  }

  def loadWorkspace() = {
    IPEEventBus.publish(LoadWorkspace(workspaceDir, BasicWorkspaceEntryDeserializer))
  }

}