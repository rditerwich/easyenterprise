package claro.cms

import net.liftweb.http.{Req,LiftRules,RulesSeq,RequestVar}
import net.liftweb.util.{Full,Empty}
import java.util.Locale

object CMS {

  type Namespace = String
  type Tag = String

  val bindings = RulesSeq[RootBinding]
  val objectBindings = RulesSeq[PartialFunction[Any,Bindings]]
  val objectTemplates = RulesSeq[PartialFunction[Template,TemplateLocator]]
  val entryPoints = RulesSeq[PartialFunction[List[String],Template]]
  val templateClasspath = RulesSeq[String]
  var templateStore = HomeDirTemplateStore 

  object locale extends RequestVar[Locale](Locale.getDefault)

  def boot = {
    templateClasspath.append("claro.cms.templates")
    
    TemplateComponent.boot
    CmsComponent.boot
	  
	LiftRules.dispatch.prepend {
	  case Dispatcher(response) => () => Full(response)
	} 
	
	LiftRules.viewDispatch.prepend {
	  case ViewDispatch(template) => Left(() => Full(SuperRootBinder.bind(null,template.xml)))
    }
  }
}
