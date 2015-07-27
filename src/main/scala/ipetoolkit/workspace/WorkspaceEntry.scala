package ipetoolkit.workspace

import javafx.scene.control.TreeItem

import ipetoolkit.util.{Identifiable, Message}

/**
 * Created by krever on 7/12/15.
 */
trait WorkspaceEntry extends Identifiable {

  def uid: String

  def treeItem[T <: WorkspaceEntry]: TreeItem[T]

  def detailsOpener: Message

}
