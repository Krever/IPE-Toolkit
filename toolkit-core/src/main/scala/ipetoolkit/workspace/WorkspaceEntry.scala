package ipetoolkit.workspace

import java.util.UUID

import ipetoolkit.util.Identifiable

import scala.xml.{Elem, Node}

trait WorkspaceEntry extends Identifiable {

  def name: String

  def toXml: scala.xml.Node

  def addChild(workspaceEntry: WorkspaceEntry, parentUid: String): Unit

  //returns true if the child was found and removed
  def removeChild(uid: String): Boolean

}

class SimpleWorkspaceEntry(val name: String) extends WorkspaceEntry {
  override def uid: String = UUID.randomUUID().toString

  private var children = List[SimpleWorkspaceEntry]()

  override def toXml: Elem =
    <workspace-entry>
      <name>{name}</name>
      <children>
        {for (child <- children) yield child.toXml}
      </children>
    </workspace-entry>

  def addChild(entry: WorkspaceEntry, parentUid: String) = { //TODO order!!
    if(this.uid == parentUid)
      children = children + entry.asInstanceOf[SimpleWorkspaceEntry]
    else
      children.foreach(_.addChild(entry, parentUid))
  }

  def removeChild(uid: String): Boolean = {
    val filtered = children.filter(_.uid == uid)
    if(children.size != filtered.size)
      return true
    val removed = children.foldLeft(false)( (result, entry) => result || entry.removeChild(uid))
    removed
  }
}

trait WorkspaceRootFactory {
  def fromXml(xml: Node): WorkspaceEntry

  def empty: WorkspaceEntry
}

object SimpleWorkspaceRootFactory extends WorkspaceRootFactory {
  override def fromXml(xml: Node): WorkspaceEntry = throw new UnsupportedOperationException

  override def empty: WorkspaceEntry = new SimpleWorkspaceEntry("root")
}