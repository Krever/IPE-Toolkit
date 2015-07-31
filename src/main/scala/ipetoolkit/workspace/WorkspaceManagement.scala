package ipetoolkit.workspace

import ipetoolkit.util.Message


sealed trait WorkspaceManagement extends Message

case class NewWorkspace(dir: String) extends WorkspaceManagement

case class SaveWorkspace() extends WorkspaceManagement

case class LoadWorkspace(dir: String) extends WorkspaceManagement

case class GetWorkspace() extends WorkspaceManagement


case class AddWorkspaceEntry(entry: WorkspaceEntry, parentUid: Option[String]) extends WorkspaceManagement

case class RemoveWorkspaceEntry(uid: String) extends WorkspaceManagement

