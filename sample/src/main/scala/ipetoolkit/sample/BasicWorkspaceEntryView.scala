package ipetoolkit.sample

import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.control.{MenuItem, ContextMenu}
import javafx.scene.layout.Pane

import ipetoolkit.details.DetailsManagement.ShowDetails
import ipetoolkit.util.Message
import ipetoolkit.workspace.{WorkspaceEntry, WorkspaceEntryView}

class BasicWorkspaceEntryView(workspaceEntry : WorkspaceEntry) extends WorkspaceEntryView{

  override def contextMenu: Option[ContextMenu] = {
    val item1 = new MenuItem("Add")
    item1.setOnAction( new EventHandler[ActionEvent](){
      override def handle(t: ActionEvent): Unit = addWorkSpaceEntry(new BasicWorkspaceEntry)
    })
    val item2 = new MenuItem("Remove")
//    item2.setOnAction( )
    Some(new ContextMenu(item1, item2))
  }

  override def detailsOpener: Option[Message] = Some(ShowDetails(this, new Pane()))

  override def model: WorkspaceEntry = workspaceEntry
}
