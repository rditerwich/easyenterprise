package claro.cms

import java.util.Date
import xml.{Elem, Node, NodeSeq, Text}
import net.liftweb.http.{ RequestVar, SessionVar }

object BindingCtor {

	type Ctor = () => Binding

	class BindingSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindingOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindingCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindingGroup(label : String, binding : Binding) extends Tuple2(label, binding)
	class StringSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class StringOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class StringCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class LongSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class LongOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class LongCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class DoubleSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class DoubleOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class DoubleCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class DateSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class DateOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class DateCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class BooleanSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindableSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindableOption(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindableCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class BindableCollectionCollection(label : String, binding : Binding) extends Tuple2(label, binding)
	class StaticXml(label : String, binding : Binding) extends Tuple2(label, binding)
	class DynamicXml(label : String, binding : Binding) extends Tuple2(label, binding)
	class RequestVarSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class SessionVarSingle(label : String, binding : Binding) extends Tuple2(label, binding)
	class ComplexSingle
	class ComplexOption
	class ComplexCollection

	private class LabeledCtor(label : String) {
		def -> (bindings : Bindings) = (label, bindings)
		def -> (ctor : Ctor) = (label, ctor()) 
		def -> (f : => Binding) = new BindingSingle(label, new BindingBinding(f))
		def -> (f : => Option[Binding]) = new BindingOption(label, new BindingOptionBinding(f))
		def -> (f : => Collection[Binding]) = new BindingCollection(label, new BindingCollectionBinding(f))
		def -> (f : => Collection[Collection[Binding]]) = new BindingGroup(label, new BindingGroupBinding(f))
		def -> (f : => String) = new StringSingle(label, new AnyBinding(f, toText))
		def -> (f : => Option[String]) = new StringOption(label, new AnyOptionBinding(f, toText))
		def -> (f : => Collection[String]) = new StringCollection(label, new AnyCollectionBinding(f, toText)) 
		def -> (f : => Double) = new DoubleSingle(label, new AnyBinding(f, toText))
		def -> (f : => Option[Double]) = new DoubleOption(label, new AnyOptionBinding(f, toText))
		def -> (f : => Collection[Double]) = new DoubleCollection(label, new AnyCollectionBinding(f, toText)) 
		def -> (f : => Date) = new DateSingle(label, new AnyBinding(f, toText))
		def -> (f : => Option[Date]) = new DateOption(label, new AnyOptionBinding(f, toText))
		def -> (f : => Collection[Date]) = new DateCollection(label, new AnyCollectionBinding(f, toText)) 
		def -> (f : => Boolean) = new BooleanSingle(label, new BooleanBinding(f))
		def -> (f : => NodeSeq) = new StaticXml(label, new XmlBinding(_ => f)) {}
		def -> (f : NodeSeq => NodeSeq) = new DynamicXml(label, new XmlBinding(f))
		def -> (f : => RequestVar[Binding]) = new RequestVarSingle(label, f.is)
		def -> (f : => SessionVar[Binding]) = new SessionVarSingle(label, f.is)
		def -> (f : => Collection[Any]) = new ComplexCollection {
			def -> (defaultPrefix : String) = (label, new ComplexCollectionBinding(f, defaultPrefix))
		}
		def -> (f : => Option[Any]) = new ComplexOption {
			def -> (defaultPrefix : String) = (label, new ComplexOptionBinding(f, defaultPrefix))
		}
		def -> (f : => Any) = new ComplexSingle {
			def -> (defaultPrefix : String) = (label, new ComplexBinding(f, defaultPrefix))
		}
	}

	def toText[A] : A => NodeSeq => NodeSeq = obj => obj match {
		case null => _ => NodeSeq.Empty
		case obj => _ => Text(obj.toString)
	}
}

trait BindingCtor {
	import BindingCtor._
	implicit def labeledCtor(label : String) = new LabeledCtor(label)
}

case class Person(name : String)

class TestComponent extends BindingCtor {
	
	val bindings : Map[String, Binding] = Map(
			"say" -> "hello",
			"say-again" -> "hello".toString,
			"is-true" -> true,
			"count" -> 12,
			"count" -> 12.asInstanceOf[Long],
			"person" -> new Person("stuart") -> "person"
	)
}