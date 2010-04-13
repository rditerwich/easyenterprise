package claro.cms

import net.liftweb.http.RulesSeq

trait Component extends BindingHelpers {
  val prefix : String
  val bindings = RulesSeq[PartialFunction[Any,Map[String,Binding]]]
  var rewrite = RulesSeq[Function[List[String],List[String]]]
  val templateLocators = RulesSeq[PartialFunction[Template,ResourceLocator]]

  implicit def toList(a : Scope) = List(a)

}
