package ipetoolkit.details

import javafx.scene.Node

import ipetoolkit.util.Message
import ipetoolkit.workspace.WorkspaceEntry


trait DetailsManagement extends Message

object DetailsManagement {

  case class ShowDetails(entry: WorkspaceEntry, content: Node) extends DetailsManagement

}
