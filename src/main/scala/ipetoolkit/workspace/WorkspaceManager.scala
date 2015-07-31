package ipetoolkit.workspace

import javafx.scene.Node
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.util.Callback

import akka.actor.{Actor, ActorLogging, Props}
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.JavaFXDispatcher
import ipetoolkit.workspace.WorkspaceManager.WorkspaceTreeCell

import scala.collection.JavaConverters._

class WorkspaceManager private(treeView: TreeView[WorkspaceEntry])(implicit eventBus: ClassBasedEventBusLike) extends Actor with ActorLogging {


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    eventBus.subscribe(self, classOf[WorkspaceManagement])
    enrichTreeViewCellFactory()
  }


  override def receive: Receive = {
    case AddWorkspaceEntry(entry, parentUidOpt) =>
      if (treeView.getRoot != null) {
        if (parentUidOpt.isEmpty)
          treeView.getRoot.getChildren.add(entry.treeItem)
        else {
          addChild(treeView.getRoot, entry.treeItem, parentUidOpt.get)
        }
      } else {
        treeView.setRoot(entry.treeItem)
      }
    case RemoveWorkspaceEntry(uid) =>
      if (treeView.getRoot.getValue.uid != uid)
        removeItem(uid, treeView.getRoot)
      else treeView.setRoot(null)
  }

  def addChild(potentialParent: TreeItem[WorkspaceEntry], child: TreeItem[WorkspaceEntry], parentUid: String): Unit = {
    if (potentialParent.getValue.uid == parentUid)
      potentialParent.getChildren.add(child)
    else {
      potentialParent.getChildren.asScala.foreach(addChild(_, child, parentUid))
    }
  }

  private def removeItem(uid: String, item: TreeItem[WorkspaceEntry]): Unit = {
    val filteredItems = item.getChildren.asScala.filter(_.getValue.uid != uid)
    item.getChildren.setAll(filteredItems.asJava)
    filteredItems.foreach(removeItem(uid, _))
  }

  private def enrichTreeViewCellFactory(): Unit = {
    //TODO nadpisujemy istniejaca fabrykę uzytkownika, wiec smutno, trzeba wymyslic lepsze rozwiazanie
    val originalCellFactory = treeView.getCellFactory
    treeView.setCellFactory(new Callback[TreeView[WorkspaceEntry], TreeCell[WorkspaceEntry]] {
      override def call(param: TreeView[WorkspaceEntry]): TreeCell[WorkspaceEntry] = new WorkspaceTreeCell
    })
  }
}

object WorkspaceManager {

  def props(treeView: TreeView[WorkspaceEntry])(implicit eventBus: ClassBasedEventBusLike): Props = Props(new WorkspaceManager(treeView)).withDispatcher(JavaFXDispatcher.Id)

  private class WorkspaceTreeCell extends TreeCell[WorkspaceEntry] {

    override def updateItem(item: WorkspaceEntry, empty: Boolean): Unit = {
      super.updateItem(item, empty)
      if (empty) {
        setText(null)
        setGraphic(null)
      } else item match {
        case newNode: Node =>
          setText(null)
          val currentNode = getGraphic
          if (currentNode == null || !currentNode.equals(newNode)) {
            setGraphic(newNode)
          }
        case _ =>
          setText(if (item == null) "null" else item.toString)
          setGraphic(null)
      }
      if (getItem != null) {
        getItem.contextMenu.foreach(setContextMenu)
      }

    }
  }

}
