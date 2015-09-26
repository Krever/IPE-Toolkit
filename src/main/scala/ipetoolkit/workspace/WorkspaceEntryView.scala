package ipetoolkit.workspace

import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.{ContextMenu, TreeItem}

import com.google.common.base.Strings
import ipetoolkit.details.DetailsManagement.ShowDetails
import ipetoolkit.util.Message

trait WorkspaceEntryView {

  val nameProperty: StringProperty = new SimpleStringProperty(uid.toString)

  val treeItem = new TreeItem[WorkspaceEntryView](this)

  def model: WorkspaceEntry

  def uid: String = model.uuid

  def contextMenu: Option[ContextMenu]

  def detailsPath: String = ""

  def addWorkSpaceEntry(workspaceEntry: WorkspaceEntry) = {
    model.addChild(workspaceEntry)
  }

  def addChildToView(entryView: WorkspaceEntryView): Unit = {
    treeItem.getChildren.add(entryView.treeItem)
  }

  def removeWorkSpaceEntry() = {
    model.dispose()
  }

  private[workspace] def removeWorkSpaceViewFromParent(workspaceEntryView: WorkspaceEntryView) = {
    treeItem.getChildren.remove(workspaceEntryView.treeItem)
  }

  def detailsOpener: Option[Message] = {
    if(!Strings.isNullOrEmpty(detailsPath)) {
      val loader = new FXMLLoader(getClass.getResource(detailsPath))
      val pane = loader.load[Node]()
      val controller = loader.getController[DetailsController]
      controller.setModel(this.model)
      Some(ShowDetails(this, pane))
    }else{
     None
    }
  }

}
