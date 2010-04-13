package claro.cms

import net.liftweb.http.{Req,LiftRules,LiftSession,LiftResponse,RulesSeq,RequestVar,S,InMemoryResponse,OkResponse,XhtmlResponse,CSSResponse}
import net.liftweb.util.{Box,Full,Empty,Log}
import xml.Node
import java.util.Locale
import javax.persistence.{EntityManager,EntityManagerFactory}
import javax.servlet.http.HttpServletRequest

object Cms {

  val templateClasspath = RulesSeq[String]
  val components = RulesSeq[() => Component]
  val locales = Set(Locale.getAvailableLocales map (_ toString) :_*) 

  object locale extends RequestVar[Locale](Locale.getDefault)

  def entityManager(name : String) = Request.site.entityManagerFactory(name).createEntityManager

  def boot = {
    
    LiftRules.calculateContextPath = Dispatch.calculateContextPath _
    
    LiftRules.statelessDispatchTable.append {
      case Dispatch(response) => response
    }
    
    LiftRules.viewDispatch.append {
    	case Dispatch(response) => Left(response)
    
    }
//	LiftRules.viewDispatch.append {
//	  case ViewDispatch(template) => Left(() => {
//		Request.site.rootBinding.bind(template.xml) match {
//		  case xml if (xml.first.label == "html") => Full(xml)
//		  case _ => Empty
//		}
//      })
//    }

    components.append(() => new TemplateComponent)
    components.append(() => new claro.cms.components.MenuComponent)
    Site
    Log.info("CMS Configuration:" + Site.sites.map(CmsInfo.siteInfo(_, "  ")).mkString("\n"))
  }

  object ViewDispatch {
    def unapply(path : List[String]) : Option[ConcreteTemplate] = Request.get match {
      case null => None
      case request => request.template
    }
  }
}

