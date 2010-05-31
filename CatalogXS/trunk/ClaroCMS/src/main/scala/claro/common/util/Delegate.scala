package claro.common.util

class Delegate[A](val delegate : A) {

  override def equals(other : Any) = other match {
    case otherDelegate : Delegate[_] => delegate equals otherDelegate.delegate
	case _ => false 
  }
  
  override def hashCode = delegate.hashCode
  
  implicit def convertToDelegate(delegate : Delegate[A]) : A = delegate.delegate
}
