package ipetoolkit.workspace

import ipetoolkit.util.Message


sealed trait WorkspaceManagement extends Message

object WorkspaceManagement {

  case class NewWorkspace(dir: String, rootEntryView: WorkspaceEntryView) extends WorkspaceManagement

  case class SaveWorkspace(dir: String) extends WorkspaceManagement

  case class LoadWorkspace(dir: String, viewCreator: WorkspaceEntry => WorkspaceEntryView) extends WorkspaceManagement

  case class LoadOrNewWorkspace(dir: String, rootEntry: WorkspaceEntry, viewCreator: WorkspaceEntry => WorkspaceEntryView) extends WorkspaceManagement


}

