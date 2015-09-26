package ipetoolkit.workspace

import java.util
import java.util.UUID
import javax.xml.bind.annotation._

import scala.collection.JavaConverters._

@XmlAccessorType(XmlAccessType.NONE)
trait WorkspaceEntry {


  protected var _children: List[WorkspaceEntry] = List()

  def children: List[WorkspaceEntry] = _children

  private var parent : WorkspaceEntry = null

  @XmlElement
  protected val _uuid = UUID.randomUUID().toString

  def uuid: String = _uuid

  val view : WorkspaceEntryView

  def addChild(workspaceEntry: WorkspaceEntry): Unit ={
    _children = _children :+ workspaceEntry
    workspaceEntry.parent = this
    view.addChildToView(workspaceEntry.view)
  }

  def removeChild(workspaceEntry: WorkspaceEntry) = {
    _children = _children.filter(!_.equals(workspaceEntry))
  }

  private[workspace] def dispose() = {
    if(parent != null){
      parent.view.removeWorkSpaceViewFromParent(this.view)
      parent.removeChild(this)
    }
  }


  @XmlElementWrapper
  @XmlAnyElement(lax = true)
  protected def getChildren(): util.Collection[WorkspaceEntry] = new util.LinkedList[WorkspaceEntry](children.asJavaCollection) {
    //JAXB doesnt use setter for collection types so we need to hack it.
    override def add(e: WorkspaceEntry): Boolean = {
      addChild(e)
      true
    }

  }


}

