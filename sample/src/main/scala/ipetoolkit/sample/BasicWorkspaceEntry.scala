package ipetoolkit.sample

import java.util.UUID
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.{ContextMenu, MenuItem, TreeItem}

import ipetoolkit.bus.{ClassBasedEventBusLike, IPEEventBus}
import ipetoolkit.util.Message
import ipetoolkit.workspace.{AddWorkspaceEntry, RemoveWorkspaceEntry, WorkspaceEntry, WorkspaceEntryDeserializer}

import scala.collection.JavaConverters._
import scala.xml.{Elem, Node}


case class BasicWorkspaceEntry(uid: String = UUID.randomUUID().toString, detailsOpener: Option[Message] = None)(implicit eventBus: ClassBasedEventBusLike) extends WorkspaceEntry {
  override val treeItem: TreeItem[WorkspaceEntry] = new TreeItem[WorkspaceEntry](this)

  override def contextMenu: Option[ContextMenu] = {
    val item1 = new MenuItem("Add")
    item1.setOnAction(new EventHandler[ActionEvent]() {
      def handle(e: ActionEvent) = {
        eventBus.publish(AddWorkspaceEntry(BasicWorkspaceEntry(), Some(uid)))
      }
    })
    val item2 = new MenuItem("Remove")
    item2.setOnAction(new EventHandler[ActionEvent]() {
      def handle(e: ActionEvent) = {
        eventBus.publish(RemoveWorkspaceEntry(uid))
      }
    })
    Some(new ContextMenu(item1, item2))
  }

  override def toXml: Option[Elem] = Some {
    <basicWorkspaceEntry>
      <uid>
        {uid}
      </uid>
      <children>
        {for (child <- treeItem.getChildren.asScala) yield child.getValue.toXml.get}
      </children>
    </basicWorkspaceEntry>
  }
}

object BasicWorkspaceEntryDeserializer extends WorkspaceEntryDeserializer {

  implicit val eventBus = IPEEventBus

  override def fromXml(xml: Node): Option[WorkspaceEntry] = {
    (xml \ "uid").headOption map { uid =>
      val entry = new BasicWorkspaceEntry(uid.text)
      val childrenNodes = (xml \ "children").headOption.map(_.child).getOrElse(List())
      val children = childrenNodes.flatMap(BasicWorkspaceEntryDeserializer.fromXml)
      entry.treeItem.getChildren.setAll(children.map(_.treeItem).asJavaCollection)
      entry
    }
  }
}