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

