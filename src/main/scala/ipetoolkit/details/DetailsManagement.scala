package ipetoolkit.details

import javafx.scene.Node

import ipetoolkit.util.Message


trait DetailsManagement extends Message

object DetailsManagement {

  case class ShowDetails(titile: String, content: Node) extends DetailsManagement

}
