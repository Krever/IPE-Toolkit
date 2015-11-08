package ipetoolkit.details

import ipetoolkit.workspace.WorkspaceEntry

/**
 * Created by humblehound on 26.09.15.
 */
trait DetailsController {

  var model: WorkspaceEntry = _

  def setModel(workspaceEntry: WorkspaceEntry) = model = workspaceEntry
}
