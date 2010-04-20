package claro.common.util

/**
 * Extensions to the Object class
 */
class RichObject[A](obj : A) {
    
  /** 
   * Convert null values to Option
   */
  def asOption = if (obj == null) None else Some(obj)
    
  def useIn(f: A => Any) : A = {
    f(obj)
    obj
  }
}

class RichDouble(value : java.lang.Double) {
  def getOrElse(default : Double) : Double = value match {
    case null => default
    case _ => value.doubleValue
  }
}