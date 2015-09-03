package ipetoolkit.workspace

import scala.collection.mutable

trait WorkspaceEntry {

  private val children = mutable.ArrayBuffer[WorkspaceEntry]()

  private var parent : WorkspaceEntry = null

  val view : WorkspaceEntryView = null

  def serialize()

  def addChild(workspaceEntry: WorkspaceEntry): Unit ={
    children += workspaceEntry
    workspaceEntry.parent = this
  }

  def removeChild(workspaceEntry: WorkspaceEntry) = {
    children -= workspaceEntry
  }

  private[workspace] def dispose() = {
    if(parent != null){
      parent.view.removeWorkSpaceViewFromParent(this.view)
      parent.removeChild(this)
    }
  }

}
