package ipetoolkit.workspace

import java.util
import java.util.UUID
import javafx.beans.property.{SimpleStringProperty, StringProperty}
import javax.xml.bind.annotation._

import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._

@XmlAccessorType(XmlAccessType.NONE)
trait WorkspaceEntry extends LazyLogging{

  protected var _children: List[WorkspaceEntry] = List()

  def children: List[WorkspaceEntry] = _children

  private var parent : WorkspaceEntry = null

  @XmlElement
  protected val _uuid = UUID.randomUUID().toString

  def uuid: String = _uuid

  var nameProperty: StringProperty = new SimpleStringProperty()

  val view : WorkspaceEntryView

  def addChild(workspaceEntry: WorkspaceEntry): Unit ={
    if( !_children.exists(entry => entry.equals(workspaceEntry)) ) {
      _children = _children :+ workspaceEntry
      workspaceEntry.parent = this
      view.addChildToView(workspaceEntry.view)
      workspaceEntry.nameProperty.bindBidirectional(workspaceEntry.view.nameProperty)
    }else{
      logger.debug("Adding duplicate workspaceEntryView", s"Trying to load ${workspaceEntry.uuid}")
    }
  }

  def removeChild(workspaceEntry: WorkspaceEntry) = {
    _children = _children.filter(!_.equals(workspaceEntry))
  }

  def delete() = {
    if(parent != null){
      parent.view.removeWorkSpaceViewFromParent(this.view)
      parent.removeChild(this)
    }
  }

  @XmlElementWrapper
  @XmlAnyElement(lax = true)
  protected def getChildren: util.Collection[WorkspaceEntry] = new util.LinkedList[WorkspaceEntry](children.asJavaCollection) {
    //JAXB doesnt use setter for collection types so we need to hack it.
    override def add(e: WorkspaceEntry): Boolean = {
      addChild(e)
      true
    }

  }

  override def equals(other: Any) = {
    other match {
      case entry: WorkspaceEntry if this.uuid.equals(entry.uuid) => true
      case _ => false
    }
  }

  @XmlElement
  def getName :String = view.nameProperty.getValue

  def setName(name :String ) = view.nameProperty.setValue(name)
}

