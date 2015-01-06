package pl.codekratisti.messages

import pl.codekratisti.model.Task

sealed trait TaskManagement extends Message

case class NewTask(task:Task, cancellationMsg:Message) extends TaskManagement

case class TaskProgressUpdate(uid:String, progress:Double) extends TaskManagement

case class TaskCancelled(uid:String) extends TaskManagement

case class CancelTask(uid:String) extends TaskManagement


