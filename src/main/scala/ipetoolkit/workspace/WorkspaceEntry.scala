package ipetoolkit.workspace

import java.util.UUID
import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javafx.scene.control.{ContextMenu, TreeItem}

import ipetoolkit.util.{Identifiable, Message}

import scala.collection.JavaConverters._

trait WorkspaceEntry extends Identifiable {

  private val uuid = UUID.randomUUID().toString

  def uid: String = uuid

  private val _treeItem = new TreeItem[WorkspaceEntry](this)
  def treeItem: TreeItem[WorkspaceEntry] = _treeItem

  def detailsOpener: Option[Message]

  def contextMenu: Option[ContextMenu]

  def toXml: Option[xml.Elem]

  val nameProperty: StringProperty = new SimpleStringProperty(uid)

  def addChild(workspaceEntry: WorkspaceEntry, parentUidOpt: Option[String]): Unit = parentUidOpt match {
    case None => treeItem.getChildren.add(workspaceEntry.treeItem)
    case Some(parentUid) if parentUid == this.uid => treeItem.getChildren.add(workspaceEntry.treeItem)
    case Some(parentUid) => treeItem.getChildren.asScala.foreach(_.getValue.addChild(workspaceEntry, parentUidOpt))
  }

  override def toString: String = uid
}

trait WorkspaceEntryDeserializer {
  def fromXml(xmlElem: xml.Node): Option[WorkspaceEntry]
}