package ipetoolkit.logs

import ipetoolkit.util.{Identifiable, Message}


/**
 * Short for notification
 */
trait Noty extends Identifiable {
  def shortMessage: String

  def longMessage: String

  def category: String

  def actions: Map[String, Message]
}

