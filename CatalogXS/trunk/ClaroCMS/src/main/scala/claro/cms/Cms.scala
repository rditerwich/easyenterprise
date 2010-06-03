package claro.cms

import net.liftweb.http.{Req,LiftRules,LiftSession,LiftResponse,RulesSeq,RequestVar,S,InMemoryResponse,OkResponse,XhtmlResponse,CSSResponse}
import net.liftweb.common.{Box,Full,Empty,Logger}
import xml.Node
import java.util.Locale
import javax.persistence.{EntityManager,EntityManagerFactory}
import javax.servlet.http.HttpServletRequest
import claro.common.util.Locales
import claro.common.util.Conversions._

object Cms {

  val components = RulesSeq[() => Component]
  val logger = Logger("CMS")

  def entityManager(name : String) = Website.instance.entityManagerFactory(name).createEntityManager

  var caching = false

  components.append(() => new TemplateComponent)
  components.append(() => new claro.cms.components.StdComponent)
  components.append(() => new claro.cms.components.Utils)
  components.append(() => new claro.cms.components.MenuComponent)

  object locale extends RequestVar[Locale](Website.instance.defaultLocale)
  
//    LiftRules.calculateContextPath = Request.calculateContextPath _
    
//    LiftRules.jsArtifacts = MyJsArtifacts

//    LiftRules.viewDispatch.append {
//    	case Dispatch(response) => Left(response)
//    }
    
//	LiftRules.viewDispatch.append {
//	  case ViewDispatch(template) => Left(() => {
//		Website.instance.rootBinding.bind(template.xml) match {
//		  case xml if (xml.first.label == "html") => Full(xml)
//		  case _ => Empty
//		}
//      })
//    }

}
