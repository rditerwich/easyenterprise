package claro.common.util
 
/**
 * Extensions to the Option class
 */
class RichOption[A](option : Option[A]) {
  def getOrNull : A = option match {
    case Some(value) => value
    case None => null.asInstanceOf[A]
  }
}
  