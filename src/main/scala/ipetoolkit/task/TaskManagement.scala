package ipetoolkit.task

import ipetoolkit.util.Message

sealed trait TaskManagement extends Message

case class TaskStarted(task: Task, cancellationMsg: Message) extends TaskManagement

case class TaskProgressUpdate(uid:String, progress:Double) extends TaskManagement

case class TaskStopped(uid: String, cause: Option[Throwable] = None) extends TaskManagement


case class CancelTask(uid: String) extends TaskManagement


