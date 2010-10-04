package claro.scalaweb

import collection.mutable
import xml.{Elem}

trait Binding {
	val label : String
	val attrs : Set[BindingAttr] = Set.empty
	val elems : Set[BindingAttr] = Set.empty
	val children : Set[Binding] = Set.empty
	val doc : Option[String] = None
}	

trait BindingAttr {
	val label : String
	val doc : Option[String] = None
}

trait BindingElem {
	val prefix : Option[String] = None
	val label : String
	val doc : Option[String] = None
}

class AbstractBinding {
	case class Elem(prefix : Option[String], label : String, doc : Option[String]) extends BindingElem
	def elem(prefix : String, label : String) = Elem(Some(prefix), label, None)
}

class AnyBinding[A](val label : String, f : => Option[A]) extends Binding {
	override val elems : Set[BindingElem] = new BindingElem {
		override val label = "else"
	}
}

class TextBinding[A](val label : String, f : => Option[String]) extends AnyBinding[String](label, f) {
	val elseElem = BindingElem("when", "empty")
	val whemEmptyElem = BindingElem("when", "empty")
}

