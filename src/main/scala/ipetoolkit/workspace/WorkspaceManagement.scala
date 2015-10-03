package ipetoolkit.workspace

import ipetoolkit.util.Message


sealed trait WorkspaceManagement extends Message

object WorkspaceManagement {

  case class NewWorkspace(dir: String, rootEntry: WorkspaceEntry) extends WorkspaceManagement

  case class SaveWorkspace(dir: String) extends WorkspaceManagement

  case class LoadWorkspace(dir: String) extends WorkspaceManagement

  case class LoadOrNewWorkspace(dir: String, rootEntry: WorkspaceEntry) extends WorkspaceManagement


}

