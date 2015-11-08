package ipetoolkit.workspace

/**
 * Created by krever on 11/7/15.
 */
trait WorkspaceEntryListener {

  def childrenChanged(newChildren: Iterable[WorkspaceEntry])

  def nameChanged(newName: String)

}
