package ipetoolkit.workspace

import java.util.UUID

import scala.collection.mutable

trait WorkspaceEntry {

  private var children = mutable.HashSet[WorkspaceEntry]() // albo var albo mutable ?

  private var parent : WorkspaceEntry = null

  val uuid = UUID.randomUUID().toString

  val view : WorkspaceEntryView = null

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
