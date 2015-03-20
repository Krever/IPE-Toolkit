package ipetoolkit.workspace

import java.io.File

import ipetoolkit.util.Message

sealed trait WorkspaceManagement extends Message

case class LoadWorkspace(directory :File) extends WorkspaceManagement

case class SaveWorkspace(directory :File) extends WorkspaceManagement

case class AddEntry(entry:WorkspaceEntry, parentUid :Option[String], detailsOpener: Message)
