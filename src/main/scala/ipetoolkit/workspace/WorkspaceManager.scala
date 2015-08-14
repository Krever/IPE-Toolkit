package ipetoolkit.workspace

import java.io.File
import javafx.scene.Node
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.util.Callback

import akka.actor.{Actor, ActorLogging, Props}
import ipetoolkit.bus.ClassBasedEventBusLike
import ipetoolkit.util.JavaFXDispatcher
import ipetoolkit.workspace.WorkspaceManager.WorkspaceTreeCell

import scala.collection.JavaConverters._

class WorkspaceManager private(treeView: TreeView[WorkspaceEntry])(implicit eventBus: ClassBasedEventBusLike) extends Actor with ActorLogging {

  private val workspaceFileName = "workspace.xml"
  private var workspaceDir: File = _

  eventBus.subscribe(self, classOf[WorkspaceManagement])
  Console.out.println("actor")
  enrichTreeViewCellFactory()

  /*  @throws[Exception](classOf[Exception])
    override def preStart(): Unit = {
    }*/

  //TODO rodzielic (add,remove)(new,load,save)
  override def receive: Receive = {
    case AddWorkspaceEntry(entry, parentUidOpt) =>
      treeView.getRoot.getValue.addChild(entry, parentUidOpt)

    case RemoveWorkspaceEntry(uid) =>
      removeItem(uid, treeView.getRoot)

    case NewWorkspace(dir, rootEntry) =>
      workspaceDir = new File(dir)
      treeView.setRoot(rootEntry.treeItem)
    case SaveWorkspace() =>
      workspaceDir.mkdirs()
      scala.xml.XML.save(new File(workspaceDir, workspaceFileName).getAbsolutePath, treeView.getRoot.getValue.toXml.get)
    case LoadWorkspace(dir, loader) =>
      val xml = scala.xml.XML.loadFile(new File(dir, workspaceFileName))
      loader.fromXml(xml) match {
        case Some(rootEntry) =>
          workspaceDir = new File(dir)
          treeView.setRoot(rootEntry.treeItem)
        case None => () //TODO
      }

  }

  private def enrichTreeViewCellFactory(): Unit = {
    //TODO nadpisujemy istniejaca fabrykę uzytkownika, wiec smutno, trzeba wymyslic lepsze rozwiazanie
    val originalCellFactory = treeView.getCellFactory
    treeView.setCellFactory(new Callback[TreeView[WorkspaceEntry], TreeCell[WorkspaceEntry]] {
      override def call(param: TreeView[WorkspaceEntry]): TreeCell[WorkspaceEntry] = new WorkspaceTreeCell
    })
  }

  private def removeItem(uid: String, item: TreeItem[WorkspaceEntry]): Unit = {
    val filteredItems = item.getChildren.asScala.filter(_.getValue.uid != uid)
    item.getChildren.setAll(filteredItems.asJava)
    filteredItems.foreach(removeItem(uid, _))
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
