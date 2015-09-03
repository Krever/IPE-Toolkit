package ipetoolkit.workspace

import java.util.UUID
import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.util.Message

trait WorkspaceEntryView {

  private val uuid = UUID.randomUUID().toString

  val treeItem = new TreeItem[WorkspaceEntryView](this)

  val nameProperty: StringProperty = new SimpleStringProperty(uid)

  def model : WorkspaceEntry

  def uid: String = uuid

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
