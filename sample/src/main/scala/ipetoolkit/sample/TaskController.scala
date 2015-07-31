package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, ListCell, ListView}
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.util.Callback

import akka.actor.ActorRef
import ipetoolkit.bus.IPEEventBus
import ipetoolkit.task.{CancelTask, Task, TaskManager}

class TaskController extends Initializable {

  @FXML
  var taskListView: ListView[Task] = _

  implicit val actorSystem = Global.actorSystem

  var taskManager: ActorRef = _


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    implicit val eventBus = IPEEventBus
    taskManager = actorSystem.actorOf(TaskManager.props(taskListView.getItems))
    taskListView.setCellFactory(new Callback[ListView[Task], ListCell[Task]] {
      override def call(param: ListView[Task]): ListCell[Task] = {
        new TaskCell(taskManager)
      }
    })
  }
}

class TaskCell(taskManager: ActorRef) extends ListCell[Task] {

  //TODO uwzglednic task.cancelRequested

  private val name: Label = new Label
  private val pane: Pane = new Pane
  private val hBox: HBox = new HBox
  private val actionBtn: Button = new Button("Cancel")
  actionBtn.setOnAction(new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent): Unit = {
      taskManager ! CancelTask(getItem.uid)
    }
  })
  hBox.getChildren.addAll(name, pane, actionBtn)
  HBox.setHgrow(pane, Priority.ALWAYS)
  setText(null)

  override def updateItem(item: Task, empty: Boolean) {
    super.updateItem(item, empty)
    setEditable(false)
    if (item != null) {
      name.setText(item.toString)
      setGraphic(hBox)
    }
    else {
      setGraphic(null)
    }
  }
}