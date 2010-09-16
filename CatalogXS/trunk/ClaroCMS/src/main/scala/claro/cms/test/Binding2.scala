package claro.cms.test2

import xml._

case class Label(label : String)

trait BindingContext


trait Binder[A] {
	def bind(obj : A, node : Node, context : BindingContext) : NodeSeq
}

trait Bindable {
	def bind(node : Node, context : BindingContext)
}

trait ComplexBindable extends Bindable {
	def prefix : String
	def bindings : Map[Label,Binding]
	def bind(node : Node, context : BindingContext) = NodeSeq.Empty 
//	def bind(node : Node, context : BindingContext) = bindChildren(node.child, attr(node, "prefix", prefix), context)
//	def bindChildren(xml : NodeSeq, prefix : String, context : BindingContext) = Binding.bind(xml, context + (prefix -> Bindings(Some(boundObject), bindings)))
}

trait ObjectBindable[A] extends Function1[A, Bindable]

object BindingHelpers {

	class LabeledBindingCtor(label : String) {
		def -> (ctor : BindingCtor) = (Label(label), ctor.binding)
	}
	
	class Complex[A](f : => A) {
		def -> (defaultPrefix : String) = new BindingCtor {
			def binding = new Binding
		}
	}
	
	trait BindingCtor {
		def binding : Binding
	}
	
	def text[A](f : => A) = new BindingCtor {
		def binding = new XmlBinding(_ => new Text(f.toString))
	}
	
	def text[A](f : => Collection[A], xml : A => NodeSeq) = new BindingCtor {
		def binding = new XmlBinding(_ => new Text(f.toString))
	}
	
	def xml(xml : Node => NodeSeq) = new BindingCtor {
		def binding = new XmlBinding(xml)
	}
	
	def textBinder[A](f : => A) = new Binder[A] {
		def bind(obj : A, node : Node, context : BindingContext) = new Text(f.toString)
	}
}

trait BindingHelpers {
	import BindingHelpers._
	implicit def labeledCtor(label : String) = new LabeledBindingCtor(label)
	implicit def binder(f : => Boolean) = text(f) 
	implicit def ctor(f : => Boolean) = text(f) 
	implicit def ctor(f : => Collection[Boolean]) = text(f) 
	implicit def ctor(f : => Int) = text(f) 
	implicit def ctor(f : => Collection[Int]) = text(f) 
	implicit def ctor(f : => String) = textBinder(f) 
	implicit def ctor(f : => NodeSeq) = xml(_ => f)
	implicit def ctor(f : Node => NodeSeq) = xml(f)
}

object Binding {
	def bind(xml : NodeSeq, context : BindingContext) = NodeSeq.Empty
}

trait Binding {
	def bind(node : Node, context : BindingContext) : NodeSeq
}

class XmlBinding(f : Node => NodeSeq) extends Binding {
	def bind(node : Node, context : BindingContext) = f(node)
}

case class Person(name : String)

class Component extends BindingHelpers {
	
	val bindings : Map[Label, Binding] = Map(
			"is-true" -> true,
			"is-all-true" -> List(true, false),
			"count" -> 12,
			"person" -> new Person("stuart") -> "person"
	)
}