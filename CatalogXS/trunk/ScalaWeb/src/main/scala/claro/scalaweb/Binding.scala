package claro.scalaweb

import collection.mutable
import xml.{Elem}

//object Binding {
//	def apply(prefix : String, label : String) = new DefaultBinding(Some(prefix), label)
//	def apply(label : String) = new DefaultBinding(None, label)
//}

class Binding(val prefix : Option[String], val label : String) {
	val attrs : Set[Binding] = Set.empty
	val children : Set[Binding] = Set.empty
	val doc : Option[String] = None
	def producer(elem : Elem) : Producer
}

class AnyBinding(prefix : Option[String], label : String) extends Binding(prefix, label) {
	val notBinding = new Binding(None, "not") {
		val doc = "Explain..."
		def producer(elem : Elem) = null
	}
	val elseBinding = new Binding(None, "else") {
		val doc = "Explain..."
			def producer(elem : Elem) = null
	}
	val attrs = super.attrs + notBinding
	val children = super.children + elseBinding
	def producer(elem : Elem) = {
		// just output text if there are no children
		if (elem.child.isEmpty) {
			
		}
	}
}

trait TextBinding extends AnyBinding {
	
}

