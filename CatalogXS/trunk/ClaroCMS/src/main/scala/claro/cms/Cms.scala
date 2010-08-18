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

  def caching = false

  components.append(() => new TemplateComponent)
  components.append(() => new claro.cms.components.StdComponent)
  components.append(() => new claro.cms.components.Utils)
  components.append(() => new claro.cms.components.MenuComponent)

  object locale extends RequestVar[Locale](Website.instance.defaultLocale)
}
