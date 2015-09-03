package ipetoolkit.sample

import java.util.UUID

import ipetoolkit.workspace.{WorkspaceEntry, WorkspaceEntryView}


case class BasicWorkspaceEntry(val uid: String = UUID.randomUUID().toString) extends WorkspaceEntry {
  override def serialize(): Unit = ???

  override def view: WorkspaceEntryView = {
    new BasicWorkspaceEntryView(this)
  }
}
