package claro.cms

import net.liftweb.http.RulesSeq

trait Component extends BindingHelpers {
  val prefix : String
  val bindings = RulesSeq[PartialFunction[Any,Map[String,Binding]]]
  val entryPoints = RulesSeq[PartialFunction[List[String],Template]]
  val templateClasspath = RulesSeq[String]
  val templateLocators = RulesSeq[PartialFunction[Template,ResourceLocator]]

  implicit def toList(a : Scope) = List(a)

}
