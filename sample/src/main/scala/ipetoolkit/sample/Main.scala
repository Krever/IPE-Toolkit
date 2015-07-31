package ipetoolkit.sample

import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, WindowEvent}


class Main extends Application {

  override def start(primaryStage: Stage): Unit = {
    val root: Parent = FXMLLoader.load(getClass.getResource("/main.fxml"))
    primaryStage.setTitle("Hello World")
    primaryStage.setScene(new Scene(root, 800, 600))
    primaryStage.show()

    primaryStage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = {
        Global.actorSystem.shutdown()
      }
    })

  }
}

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)
  }
}
