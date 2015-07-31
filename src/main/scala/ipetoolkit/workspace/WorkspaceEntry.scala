package ipetoolkit.workspace

import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.util.{Identifiable, Message}

trait WorkspaceEntry extends Identifiable {

  def uid: String

  def treeItem: TreeItem[WorkspaceEntry]

  def detailsOpener: Option[Message]

  def contextMenu: Option[ContextMenu]

}
