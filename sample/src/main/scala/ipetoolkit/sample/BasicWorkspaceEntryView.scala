package ipetoolkit.sample

import javafx.beans.property.SimpleStringProperty
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{ContextMenu, MenuItem}

import ipetoolkit.workspace.{WorkspaceEntry, WorkspaceEntryView}

class BasicWorkspaceEntryView(workspaceEntry : WorkspaceEntry, override val nameProperty : SimpleStringProperty) extends WorkspaceEntryView{

  override def contextMenu: Option[ContextMenu] = {
    val item1 = new MenuItem("Add")
    item1.setOnAction( new EventHandler[ActionEvent](){
      override def handle(t: ActionEvent): Unit = addWorkSpaceEntry(new BasicWorkspaceEntry)
    })
    val item2 = new MenuItem("Remove")
    item2.setOnAction( new EventHandler[ActionEvent](){
      override def handle(t: ActionEvent): Unit = removeWorkSpaceEntry()
    })
    Some(new ContextMenu(item1, item2))
  }

  override def model: WorkspaceEntry = workspaceEntry

  override def detailsPath: String = "/testTabView.fxml"
}
