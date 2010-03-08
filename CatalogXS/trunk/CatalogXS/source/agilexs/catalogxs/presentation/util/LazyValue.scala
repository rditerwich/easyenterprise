package agilexs.catalogxs.presentation.util

import agilexs.catalogxs.presentation.model.Conversions._

class LazyValue[A](f: => A) {
  private var value : Option[A] = None
  
  def get : A = value match {
    case Some(a) => a
    case None => f useIn (a => value = Some(a))
  }
  
  def reset = value = None
}
