package claro.cms.test

import xml._

trait Transformer {
	def transform(xml : Node) : NodeSeq
}

case class Bindings(obj : Object, transformers : Map[String, Transformer])
trait Bindable
case class ComplexBindable(bindable : Bindable, prefix : String)
case class AnyBindable(obj : Any) extends Bindable

class AnyTransformer(obj : => Any) extends Transformer {
	def transform(xml : Node) : NodeSeq = null
}


case class Label(label : String)
case class Prefix(label : String)

class Product


object Test {
	val product : Product = null
	
	implicit def label(label : String) = Label(label)
	implicit def prefix(prefix : String) = Prefix(prefix)
	
	implicit def bindable(obj : Any) = AnyBindable(obj)
	implicit def transformer(obj : Any) = new AnyTransformer(obj)
	implicit def complex(p : ((String, Bindable),String)) = (p._1._1, ComplexBindable(p._1._2, p._2))
	def complex(bindable : Bindable)(prefix : String) = ComplexBindable(bindable, prefix)
	

	bindings
	
	val bindings : Map[Label, Transformer] = Map(
//	 "product" -> product,
//	 "product" -> product -> "product"
	)
}