package ipetoolkit.workspace

import java.util
import java.util.UUID
import javax.xml.bind.annotation._

import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._

@XmlAccessorType(XmlAccessType.NONE)
trait WorkspaceEntry extends LazyLogging{

  protected var name: String = _

  protected var _children: List[WorkspaceEntry] = List()
  def children: List[WorkspaceEntry] = _children

  private var parent : WorkspaceEntry = null

  @XmlElement
  protected val _uuid = UUID.randomUUID().toString
  def uuid: String = _uuid

  def addChild(workspaceEntry: WorkspaceEntry): Unit ={
    if( !_children.exists(entry => entry.equals(workspaceEntry)) ) {
      _children = _children :+ workspaceEntry
      workspaceEntry.parent = this
      childrenChanged()
    }else{
      logger.debug("Adding duplicate workspaceEntryView", s"Trying to load ${workspaceEntry.uuid}")
    }
  }

  def removeChild(workspaceEntry: WorkspaceEntry) = {
    _children = _children.filter(!_.equals(workspaceEntry))
    childrenChanged()
  }

  def remove() = {
    if(parent != null){
      parent.removeChild(this)
    }
  }


  @XmlElementWrapper
  @XmlAnyElement(lax = true)
  protected def getChildren: util.Collection[WorkspaceEntry] = new util.LinkedList[WorkspaceEntry](children.asJavaCollection) {
    //JAXB doesnt use setter for collection types so we need to hack it.
    override def add(e: WorkspaceEntry): Boolean = {
      addChild(e); true
    }
  }

  @XmlElement
  def getName: String = name

  def setName(name: String) {
    this.name = name
    listeners.foreach(_.nameChanged(name))
  }

  var listeners = List[WorkspaceEntryListener]()

  def addListener(listener: WorkspaceEntryListener): Unit = {
    listeners :+= listener
  }

  def childrenChanged() = listeners.foreach(_.childrenChanged(children))


  override def equals(other: Any) = {
    other match {
      case entry: WorkspaceEntry if this.uuid.equals(entry.uuid) => true
      case _ => false
    }
  }
}

