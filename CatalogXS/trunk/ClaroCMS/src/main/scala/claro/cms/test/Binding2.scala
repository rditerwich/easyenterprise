package claro.cms.test

import xml._

case class Label(label : String)

trait BindingContext

trait Bindable {
	def bind(node : Node, context : BindingContext)
}

trait ComplexBindable extends Bindable {
	
}

trait ObjectBindable[A] extends Function1[A, Bindable]

trait BindingHelpers {
	
}

trait Binding {
	def bind(node : Node, context : BindingContext)
}

class Component extends Bindinghelpers {
	
	val bindings : Map[Label, Binding] = Map(
			"is-true" -> true
	)
}