package ipetoolkit.workspace

import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.util.Message

trait WorkspaceEntryView {

  val nameProperty: StringProperty = new SimpleStringProperty(uid.toString)

  val treeItem = new TreeItem[WorkspaceEntryView](this)

  def model: WorkspaceEntry

  def uid: String = model.uuid

  def contextMenu: Option[ContextMenu]

  def detailsOpener: Option[Message]

  def addWorkSpaceEntry(workspaceEntry: WorkspaceEntry) = {
    model.addChild(workspaceEntry)
    treeItem.getChildren.add(workspaceEntry.view.treeItem)
  }

  def removeWorkSpaceEntry() = {
    model.dispose()
  }

  private[workspace] def removeWorkSpaceViewFromParent(workspaceEntryView: WorkspaceEntryView) = {
    treeItem.getChildren.remove(workspaceEntryView.treeItem)
  }


}
