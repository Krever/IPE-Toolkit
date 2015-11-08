package ipetoolkit.workspace

import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.details.DetailsController
import ipetoolkit.details.DetailsManagement.ShowDetails
import ipetoolkit.util.Message

import scala.collection.JavaConverters._

trait WorkspaceEntryView {

  //MUST BE OVERRIDEN IN CONSTRUCTOR
  def model: WorkspaceEntry

  setUpModelSync()

  def childrenToViews: PartialFunction[WorkspaceEntry, WorkspaceEntryView]


  lazy val nameProperty: StringProperty = new SimpleStringProperty(model.getName)

  val treeItem = new TreeItem[WorkspaceEntryView](this)
  updateTreeItemChildren(model.children)

  def uid: String = model.uuid

  def contextMenu: Option[ContextMenu] = None

  def detailsPath: Option[String] = None

  def addChild(workspaceEntry: WorkspaceEntry) = model.addChild(workspaceEntry)

  def remove() = model.remove()

  def detailsOpener: Option[Message] = {
    detailsPath.map { path =>
      val loader = new FXMLLoader(getClass.getResource(path))
      val pane = loader.load[Node]()
      val controller = loader.getController[DetailsController]
      controller.setModel(this.model)
      ShowDetails(this, pane)
    }
  }

  private def setUpModelSync(): Unit = {
    model.addListener(new WorkspaceEntryListener {
      override def nameChanged(newName: String): Unit = nameProperty.setValue(newName)

      override def childrenChanged(newChildren: Iterable[WorkspaceEntry]): Unit = updateTreeItemChildren(newChildren)
    })

    nameProperty.addListener(new ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit =
        model.setName(newValue)
    })
  }

  private def updateTreeItemChildren(newChildren: Iterable[WorkspaceEntry]) =
    treeItem.getChildren.setAll(newChildren.map(childrenToViews).map(_.treeItem).asJavaCollection)
}
