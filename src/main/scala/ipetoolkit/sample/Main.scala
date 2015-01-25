package ipetoolkit.sample

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage


class Main extends Application {

  override def start(primaryStage: Stage): Unit = {
    val root: Parent = FXMLLoader.load(getClass.getResource("/main.fxml"))
    primaryStage.setTitle("Hello World")
    primaryStage.setScene(new Scene(root, 600, 550))
    primaryStage.show()
  }

  override def stop(): Unit = {
    Global.actorSystem.shutdown()
    super.stop()
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)
  }
}