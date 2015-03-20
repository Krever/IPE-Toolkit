package ipetoolkit.workspace


import java.io.File

import ipetoolkit.util.{Manager, Message}
import scala.xml.XML



trait WorkspaceManager {
  def openEntryDetails(uid: String): Unit

  def removeEntry(uid: String): Unit
}

/**
 * Base class for creating workspace managers.
 *
 * When overriding {{{removeEntry}}} one from this class should be called.
 */
class WorkspaceManagerBase(private val workspaceRoot: WorkspaceEntry = new SimpleWorkspaceEntry("root"),
                           private val workspaceFileName: String = "workspace.xml")
  extends Manager[WorkspaceManagement] with WorkspaceManager {

  private var detailsOpeners = Map[String, Message]()

  override def openEntryDetails(uid: String): Unit = detailsOpeners.get(uid).foreach(eventBus.publish)

  override def manage(message: WorkspaceManagement): Unit = message match {
    case SaveWorkspace(dir) => ()//XML.save(dir.getAbsolutePath, workspaceRoot.toXml)
    case LoadWorkspace(dir) => XML.loadFile(new File(dir, workspaceFileName))
    case AddEntry(entry, parentUidOpt, detailsOpener) =>
      detailsOpeners = detailsOpeners + ((entry.uid, detailsOpener))
      workspaceRoot.addChild(entry, parentUidOpt.getOrElse(workspaceRoot.uid)) //add root when parent is not given
  }

  override def removeEntry(uid: String): Unit = {
    detailsOpeners = detailsOpeners - uid
    workspaceRoot.removeChild(uid)
  }
}


