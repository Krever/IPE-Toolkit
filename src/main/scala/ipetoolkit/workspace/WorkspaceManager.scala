package ipetoolkit.workspace

import java.io.File
import javafx.event.EventHandler
import javafx.scene.control.{TreeCell, TreeView}
import javafx.scene.input.MouseEvent
import javafx.util.Callback

import akka.actor.{Actor, ActorLogging, Props}
import ipetoolkit.bus.ClassBasedEventBus
import ipetoolkit.util.JavaFXDispatcher
import ipetoolkit.workspace.WorkspaceManagement.NewWorkspace
import ipetoolkit.workspace.WorkspaceManager.WorkspaceTreeCell

class WorkspaceManager private(treeView: TreeView[WorkspaceEntryView])(implicit eventBus: ClassBasedEventBus) extends Actor with ActorLogging {

  private var workspaceDir: File = _

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
    case NewWorkspace(dir, rootEntry) =>
      workspaceDir = new File(dir)
      treeView.setRoot(rootEntry.view.treeItem)
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
