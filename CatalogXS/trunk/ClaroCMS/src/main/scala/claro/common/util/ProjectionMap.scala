package claro.common.util

import scala.collection.mutable

object ProjectionMap {
  def apply[A, B](init: A => B) = new ProjectionMap(init)
}

class ProjectionMap[A, B](init: A => B) extends mutable.HashMap[A, B] {
	override def apply(a : A) = {
	  getOrElseUpdate(a, init(a))
	}
} 
 