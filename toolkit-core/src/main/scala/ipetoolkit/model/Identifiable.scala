package ipetoolkit.model

/**
 * Identifiable object
 */
trait Identifiable {
  /**
   * @return unique identifier
   */
  def uid:String
}

object Identifiable {
  def findIndex[T <: Identifiable](uid:String, list:Iterable[T]) :Option[Int]= {
    list.zipWithIndex.find(_._1.uid == uid) match {
      case Some((elem, i)) => Some(i)
      case None => None
    }
  }
}
