package ipetoolkit.workspace

import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.util.{Identifiable, Message}

import scala.collection.JavaConverters._

trait WorkspaceEntry extends Identifiable {

  def uid: String

  private val _treeItem = new TreeItem[WorkspaceEntry](this)

  def treeItem: TreeItem[WorkspaceEntry] = _treeItem

  def detailsOpener: Option[Message]

  def contextMenu: Option[ContextMenu]

  def toXml: Option[xml.Elem]

  def addChild(workspaceEntry: WorkspaceEntry, parentUidOpt: Option[String]): Unit = parentUidOpt match {
    case None => treeItem.getChildren.add(workspaceEntry.treeItem)
    case Some(parentUid) if parentUid == this.uid => treeItem.getChildren.add(workspaceEntry.treeItem)
    case Some(parentUid) => treeItem.getChildren.asScala.foreach(_.getValue.addChild(workspaceEntry, parentUidOpt))
  }

}

trait WorkspaceEntryDeserializer {
  def fromXml(xmlElem: xml.Node): Option[WorkspaceEntry]
}