package ipetoolkit.task

import ipetoolkit.util.Identifiable

/**
 * Base class for long running tasks.
 *
 * @param uid unique id
 * @param name name
 * @param description description
 * @param progress Value between 0 and 1 for progress indication, -1 for undefined
 */
case class Task(uid: String, name: String, description: Option[String], progress: Double) extends Identifiable
