package claro.cms

import net.liftweb.http.RulesSeq

trait Component {
  val prefix : String
  val bindings = RulesSeq[PartialFunction[Any,Map[String,Binding]]]
  val entryPoints = RulesSeq[PartialFunction[List[String],Template]]
  val templateClasspath = RulesSeq[String]
  val templateLocators = RulesSeq[PartialFunction[Template,ResourceLocator]]
  
  implicit def ctor(label : String) = new BindingCtor(label)
  implicit def labeledBinding(ctor : AnyBindingCtor) = ctor.toLabeledBinding
  implicit def labeledBinding(ctor : CollectionBindingCtor) = ctor.toLabeledBinding
  
  def @@(attr : String) = "not defined"
  def @@(attr : String, default : String) = default

}
