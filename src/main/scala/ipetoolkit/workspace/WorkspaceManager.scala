package ipetoolkit.workspace

import java.io.File
import javafx.event.EventHandler
import javafx.scene.control.{TreeCell, TreeView}
import javafx.scene.input.MouseEvent
import javafx.util.Callback

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import ipetoolkit.bus.ClassBasedEventBus
import ipetoolkit.util.JavaFXDispatcher
import ipetoolkit.workspace.WorkspaceManagement.{LoadOrNewWorkspace, LoadWorkspace, NewWorkspace, SaveWorkspace}
import ipetoolkit.workspace.WorkspaceManager.WorkspaceTreeCell
import ipetoolkit.workspace.WorkspacePersistence.{Load, Loaded, Persist}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class WorkspaceManager private(treeView: TreeView[WorkspaceEntryView])(implicit eventBus: ClassBasedEventBus) extends Actor with ActorLogging {

  private val persistence = context.actorOf(Props[WorkspacePersistence])
  private implicit val timeout = Timeout(30 seconds)

  import context.dispatcher

  eventBus.subscribe(self, classOf[WorkspaceManagement])
  enrichTreeViewCellFactory()

  treeView.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = if (event.getClickCount > 1) {
      val item = treeView.getSelectionModel.getSelectedItem
      item.getValue.detailsOpener.foreach(eventBus.publish)
    }
  })

  private def enrichTreeViewCellFactory(): Unit = {
    val originalCellFactory = treeView.getCellFactory
    treeView.setCellFactory(new Callback[TreeView[WorkspaceEntryView], TreeCell[WorkspaceEntryView]] {
      override def call(param: TreeView[WorkspaceEntryView]): TreeCell[WorkspaceEntryView] = new WorkspaceTreeCell
    })
  }

  override def receive: Receive = {
    case LoadOrNewWorkspace(dir, rootEntry, createView) =>
      if (new File(dir).exists())
        self ! LoadWorkspace(dir, createView)
      else
        self ! NewWorkspace(dir, createView(rootEntry))
    case NewWorkspace(dir, rootEntryView) =>
      treeView.setRoot(rootEntryView.treeItem)
      persistence ! Persist(rootEntryView.model, new File(dir))

    case SaveWorkspace(dir) => persistence ! Persist(treeView.getRoot.getValue.model, new File(dir))

    case LoadWorkspace(dir, createView) => (persistence ? Load(new File(dir))).mapTo[Loaded].foreach {
      case Loaded(Success(rootEntry)) => treeView.setRoot(createView(rootEntry).treeItem)
      case Loaded(Failure(e)) => e.printStackTrace()
    }
  }

}

object WorkspaceManager {

  def props(treeView: TreeView[WorkspaceEntryView])(implicit eventBus: ClassBasedEventBus): Props = Props(new WorkspaceManager(treeView)).withDispatcher(JavaFXDispatcher.Id)

  private class WorkspaceTreeCell extends TreeCell[WorkspaceEntryView] {

    override def updateItem(item: WorkspaceEntryView, empty: Boolean): Unit = {
      if (getItem != null)
        textProperty().unbindBidirectional(getItem.nameProperty)
      super.updateItem(item, empty)
      if (empty) {
        setText(null)
        setGraphic(null)
      } else {
        textProperty().bindBidirectional(item.nameProperty)
        setGraphic(null)
      }
      if (getItem != null) {
        getItem.contextMenu.foreach(setContextMenu)
      }

    }
  }

}
