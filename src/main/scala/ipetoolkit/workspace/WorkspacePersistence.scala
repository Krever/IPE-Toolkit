package ipetoolkit.workspace

import java.io.File
import java.util
import javax.xml.bind.annotation.adapters.{XmlAdapter, XmlJavaTypeAdapter}
import javax.xml.bind.annotation.{XmlAnyElement, XmlElementWrapper, XmlRootElement}
import javax.xml.bind.{JAXBContext, Marshaller}

import akka.actor.Actor

import scala.collection.JavaConverters._
import scala.util.Try

private[workspace] object WorkspacePersistence {

  case class Persist(rootEntry: WorkspaceEntry, file: File)

  case class Load(file: File)

  case class Loaded(workspaceEntry: Try[WorkspaceEntry])


  private[WorkspacePersistence] def createWorkspace(entriesClasses: Iterable[Class[_]], entry: WorkspaceEntry) = new Workspace(entriesClasses, entry)

  @XmlRootElement(name = "workspace")
  private class Workspace {

    @XmlElementWrapper
    @XmlJavaTypeAdapter(classOf[ClassAdapter])
    var entriesClasses: java.util.LinkedList[Class[_]] = _

    @XmlAnyElement(lax = true)
    var root: Object = _

    def this(_entriesClasses: Iterable[Class[_]], _root: WorkspaceEntry) = {
      this()
      entriesClasses = new util.LinkedList[Class[_]](_entriesClasses.asJavaCollection)
      root = _root
    }
  }

  private class ClassAdapter extends XmlAdapter[String, Class[_]] {
    override def marshal(v: Class[_]): String = v.getCanonicalName

    override def unmarshal(v: String): Class[_] = this.getClass.getClassLoader.loadClass(v)
  }


}

class WorkspacePersistence extends Actor {

  import WorkspacePersistence._

  override def receive: Receive = {
    case Persist(entry, file) =>

      val nodesClasses = getAllNodesClasses(entry)
      import WorkspacePersistence.Workspace
      val workspaceWrapper = createWorkspace(nodesClasses, entry)

      val jaxbContext = JAXBContext.newInstance(classOf[Workspace] :: nodesClasses.toList: _*)
      val jaxbMarshaller = jaxbContext.createMarshaller()
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

      jaxbMarshaller.marshal(workspaceWrapper, file)

    case Load(file) => sender() ! Loaded(Try {
      val workspacePreParsed = JAXBContext.newInstance(classOf[Workspace]).createUnmarshaller().unmarshal(file).asInstanceOf[Workspace]
      val unmarshallingContext = JAXBContext.newInstance(classOf[Workspace] :: workspacePreParsed.entriesClasses.asScala.toList: _*)
      val jaxbUnmarshaller = unmarshallingContext.createUnmarshaller()
      val root = jaxbUnmarshaller.unmarshal(file).asInstanceOf[Workspace].root.asInstanceOf[WorkspaceEntry]
      root
    })
  }


  private def getAllNodesClasses(entry: WorkspaceEntry): Set[Class[_]] = {
    val childrenClasses = entry.children.map(_.getClass)
    val progenyClasses = entry.children.flatMap(getAllNodesClasses)
    Set(entry.getClass) ++ childrenClasses.toSet ++ progenyClasses.toSet
  }

}
