package ipetoolkit.workspace

/**
 * Created by krever on 7/11/15.
 */
sealed trait WorkspaceManagement

case class NewWorkspace(dir: String) extends WorkspaceManagement

case class SaveWorkspace() extends WorkspaceManagement

case class LoadWorkspace(dir: String) extends WorkspaceManagement

case class GetWorkspace() extends WorkspaceManagement


case class AddWorkspaceEntry(entry: WorkspaceEntry) extends WorkspaceManagement

case class RemoveWorkspaceEntry(uid: String) extends WorkspaceManagement

