package ipetoolkit.sample

import ipetoolkit.workspace.WorkspaceEntry


case class BasicWorkspaceEntry() extends WorkspaceEntry {

  override val view = new BasicWorkspaceEntryView(this)

  override def serialize(): Unit = ???

}
