package ipetoolkit.details

import javafx.scene.Node

import ipetoolkit.util.Message
import ipetoolkit.workspace.WorkspaceEntryView


trait DetailsManagement extends Message

object DetailsManagement {

  case class ShowDetails(entry: WorkspaceEntryView, content: Node) extends DetailsManagement

}
