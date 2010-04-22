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

  def entityManager(name : String) = Request.website.entityManagerFactory(name).createEntityManager

  def boot = {
    System.setProperty("websites", ",classpath:/claro/cms/showcase/,classpath:/claro/cms/webshop/website,classpath:agilexs.cms.shop.tetterode.website,classpath:claro.cms.documentation,classpath:agilexs.cms.shop.xingraphics.website")

    
    LiftRules.calculateContextPath = Request.calculateContextPath _
    
    LiftRules.statelessDispatchTable.append(Dispatch)
    LiftRules.viewDispatch.append(ViewDispatch)

//    LiftRules.viewDispatch.append {
//    	case Dispatch(response) => Left(response)
//    }
    
//	LiftRules.viewDispatch.append {
//	  case ViewDispatch(template) => Left(() => {
//		Request.website.rootBinding.bind(template.xml) match {
//		  case xml if (xml.first.label == "html") => Full(xml)
//		  case _ => Empty
//		}
//      })
//    }

    components.append(() => new TemplateComponent)
    components.append(() => new claro.cms.components.Utils)
    components.append(() => new claro.cms.components.MenuComponent)
    Website
    Log.info("CMS Configuration:" + Website.websites.map(CmsInfo.websiteInfo(_, "  ")).mkString("\n"))
  }
}

