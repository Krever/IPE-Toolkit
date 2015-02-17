package ipetoolkit.task

import ipetoolkit.util.Message

sealed trait TaskManagement extends Message

case class TaskStarted(task: Task, cancellationMsg: Message) extends TaskManagement

case class TaskProgressUpdate(uid:String, progress:Double) extends TaskManagement

case class TaskCancelled(uid:String) extends TaskManagement


