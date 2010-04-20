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

class RichInt(value : java.lang.Integer) {
  def getOrElse(default : Int) : Int = value match {
  case null => default
  case _ => value.intValue
  }
}

class RichLong(value : java.lang.Long) {
  def getOrElse(default : Long) : Long = value match {
  case null => default
  case _ => value.longValue
  }
}

class RichFloat(value : java.lang.Float) {
  def getOrElse(default : Float) : Float = value match {
    case null => default
    case _ => value.floatValue
  }
}

class RichDouble(value : java.lang.Double) {
  def getOrElse(default : Double) : Double = value match {
    case null => default
    case _ => value.doubleValue
  }
}

