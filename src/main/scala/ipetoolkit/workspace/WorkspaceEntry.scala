package ipetoolkit.workspace

import scala.collection.mutable

trait WorkspaceEntry {

  private val children = mutable.MutableList[WorkspaceEntry]()

  def serialize()

  def addChild(workspaceEntry: WorkspaceEntry): Unit ={
    children += workspaceEntry
  }

  def view : WorkspaceEntryView
}
