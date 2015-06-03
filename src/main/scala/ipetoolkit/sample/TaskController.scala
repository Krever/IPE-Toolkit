package ipetoolkit.sample

import java.net.URL
import java.util.ResourceBundle
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, Label, ListCell, ListView}
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.util.Callback

import akka.actor.TypedActor
import ipetoolkit.bus.IPEEventBus
import ipetoolkit.task.{JFXTaskManager, TaskManager}
import ipetoolkit.task.Task

class TaskController extends Initializable {

  @FXML
  var taskListView: ListView[Task] = _

  implicit val actorSystem = Global.actorSystem
  val typedExtension = TypedActor(actorSystem)

  var taskManager: TaskManager = null


  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    implicit val eventBus = IPEEventBus
    taskManager = typedExtension.typedActorOf(JFXTaskManager.typedProps(taskListView.getItems))
    taskListView.setCellFactory(new Callback[ListView[Task], ListCell[Task]] {
      override def call(param: ListView[Task]): ListCell[Task] = {
        new TaskCell(taskManager)
      }
    })
  }
}

class TaskCell(taskManager: TaskManager) extends ListCell[Task] {
  private val name: Label = new Label
  private val pane: Pane = new Pane
  private val hBox: HBox = new HBox
  private val actionBtn: Button = new Button("Cancel")
  actionBtn.setOnAction(new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent): Unit = {
      taskManager.cancelTask(getItem.uid)
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