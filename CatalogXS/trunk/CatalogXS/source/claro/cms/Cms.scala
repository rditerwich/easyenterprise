package claro.cms

import net.liftweb.http.{Req,LiftRules,LiftSession,RulesSeq,RequestVar,S}
import net.liftweb.util.{Full,Empty}
import java.util.Locale
import javax.persistence.{EntityManager,EntityManagerFactory}
import javax.servlet.http.HttpServletRequest

object Cms {

  val templateClasspath = RulesSeq[String]
  val components = RulesSeq[() => Component]
  val locales = Set(Locale.getAvailableLocales map (_ toString) :_*) 

  object locale extends RequestVar[Locale](Locale.getDefault)
  object requestData extends RequestVar[RequestData](null)
  def site = requestData.get.site
  def path = requestData.get.path
  def contextPath = requestData.get.contextPath

  def entityManager(name : String) = site.entityManagerFactory(name).createEntityManager

  def boot = {
//    templateClasspath.append("claro.cms.templates")
    
    components.append(() => new TemplateComponent)
    components.append(() => new claro.cms.components.MenuComponent)
    Site

    LiftRules.calculateContextPath = request => {
      val cmsRequest = new RequestData(request)
      Cms.requestData.set(cmsRequest)
      Full(cmsRequest.contextPath)
    } 
    
	LiftRules.viewDispatch.append {
	  case ViewDispatch(template) => Left(() => Full(site.rootBinding.bind(template.xml)))
    }
  }

  object ViewDispatch {
    def unapply(path : List[String]) : Option[ConcreteTemplate] = {
      requestData.get.template
    }
  }

  class RequestData(request : HttpServletRequest) {
    val fullPath = request.getServletPath
    val site = Site.findSite(request.getServerName,fullPath)
    println("Found site: " + site.location)
    val path = fullPath.substring(site.contextPath.size) match { case "" => "index" case path => path }
    val contextPath = site.contextPath
    val template = site.templateCache(Template(path), Cms.locale.get)
  }
}

