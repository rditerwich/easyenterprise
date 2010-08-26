package claro.cms

import net.liftweb.http.{RulesSeq,LiftResponse}

trait Component extends BindingHelpers {
  val prefix : String
  val bindings = RulesSeq[PartialFunction[Any,Map[String,Binding]]]
  val dispatch = RulesSeq[PartialFunction[(List[String],String),LiftResponse]]
  val rewrite = RulesSeq[Function[List[String],List[String]]]
  val templateLocators = RulesSeq[PartialFunction[Template,ResourceLocator]]
  def boot : Any = null

  implicit def toList(a : Scope) = List(a)

}
