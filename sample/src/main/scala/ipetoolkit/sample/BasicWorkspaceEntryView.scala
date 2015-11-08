package ipetoolkit.sample

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{ContextMenu, MenuItem}

import ipetoolkit.workspace.{WorkspaceEntry, WorkspaceEntryView}

class BasicWorkspaceEntryView(val model: WorkspaceEntry) extends WorkspaceEntryView {

  override def contextMenu: Option[ContextMenu] = {
    val item1 = new MenuItem("Add")
    item1.setOnAction( new EventHandler[ActionEvent](){
      override def handle(t: ActionEvent): Unit = addChild(new BasicWorkspaceEntry)
    })
    val item2 = new MenuItem("Remove")
    item2.setOnAction( new EventHandler[ActionEvent](){
      override def handle(t: ActionEvent): Unit = remove()
    })
    Some(new ContextMenu(item1, item2))
  }

  override def detailsPath = Some("/testTabView.fxml")

  override def childrenToViews: PartialFunction[WorkspaceEntry, WorkspaceEntryView] = {
    case x: BasicWorkspaceEntry => new BasicWorkspaceEntryView(x)
  }
}
