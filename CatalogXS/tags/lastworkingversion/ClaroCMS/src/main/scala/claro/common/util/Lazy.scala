package claro.common.util

import claro.common.util.Conversions._

object Lazy {
  def apply[A]() = new Lazy1[A]()
  def apply[A](f : => A) = new Lazy2[A](f)
}

class Lazy1[A] private[util] {
  private var value : Option[A] = None
  
  def getOrElse(f : => A) = value match {
  case Some(a) => a
  case None => f useIn (a => value = Some(a))
  }
  
  def reset = value = None
}

class Lazy2[A] private[util] (f: => A) {
  private var value : Option[A] = None
  
  def get : A = value match {
    case Some(a) => a
    case None => f useIn (a => value = Some(a))
  }
  
  def reset = value = None
}
