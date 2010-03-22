package claro.cms

import scala.xml.{NodeSeq,XML,Text}
import scala.collection.{mutable}
import java.io.File
import java.util.Locale
import java.util.concurrent.{ConcurrentHashMap,TimeUnit}
import claro.common.util.Locales
import claro.cms.Conversions._
import claro.common.util.Conversions._

object TemplateComponent {
  
  def boot = {
    CMS.bindings.append("template" -> new Bindings(
      "include" -> includeTemplate))
  }
  
  def includeTemplate : NodeSeq  = {
    val template = BindAttr("template")
    TemplateCache.findTemplate(Template(template), CMS.locale.get) match {
      case Some(template) => template.xml
      case None => Text("Error: Template not found: " + template) 
    }
  }
}

/**
 * Note the implicit conversion from a single scope to a scope list
 */
case class TemplateLocator(name : String, scopes : Iterable[Scope]*) {
}

object Scope {
  def apply(id : Any) = new Scope("", id)
  def apply(t : (String,Any)) = new Scope(t._1,t._2)
  def apply() = global
  private val global = new Scope("",null) 
}

case class Scope (name : String, id : Any) {
}

object Template {
  def apply(name : String) = new Template(name, null)
}

case class Template(name : String, obj : Any) {
}

case class ConcreteTemplate(name : String, scope : Scope, locale : Locale, xml : NodeSeq) {
}

object TemplateCache {

  private val objectTemplateCache = new ConcurrentHashMap[(Template,Locale),Option[ConcreteTemplate]]()

  def findTemplate(template : Template, locale : Locale) : Option[ConcreteTemplate] = {
//    objectTemplateCache getOrElseUpdate ((template,locale), 
      locate(templateLocator(template), Locales.getAlternatives(locale))
//    )
  }
  
  private def templateLocator(template : Template) : TemplateLocator = {
    for (rule <- CMS.objectTemplates.toList; if rule.isDefinedAt(template)) {
      return rule(template)
    }
    TemplateLocator(template.name, Scope())
  }
  
  private def locate(locator : TemplateLocator, locales : List[Locale]) : Option[ConcreteTemplate] = {
    for (store <- CMS.templateStore :: ClasspathTemplateStore :: Nil) {
      for (scopes <- locator.scopes; scope <- scopes) {
        for (locale <- locales) {
          store.getTemplate(locator.name, scope, locale) match {
            case Some(xml) => return Some(ConcreteTemplate(locator.name, scope, locale, xml))
            case None =>
          }
        }
      }
    }
    None
  }

  private[cms] def flushCache(templateName : String) = {
    val it = objectTemplateCache.keySet.iterator
    while (it.hasNext) {
      if (it.next._1.name== templateName) {
        it.remove
      }
    }
  }
}

trait TemplateStore {
  def getTemplate(name : String, scope : Scope, locale : Locale) : Option[NodeSeq]
  def storeTemplate(name : String, scope : Scope, locale : Locale, xml : Option[NodeSeq]) : Unit
  def isReadOnly : Boolean 
}

object HomeDirTemplateStore extends TemplateStore {
  
  val homeDir = new File(System.getProperty("user.home"))

  def getTemplate(name : String, scope : Scope, locale : Locale) : Option[NodeSeq] = {
    None
  }
  
  def storeTemplate(name : String, scope : Scope, locale : Locale, xml : Option[NodeSeq]) = {
  }
  
  def isReadOnly = false 
  
}

object ClasspathTemplateStore extends TemplateStore {

  def getTemplate(name : String, scope : Scope, locale : Locale) : Option[NodeSeq] = {
    val builder = new StringBuilder(name)
    if (scope.name != "") builder.append("-").append(scope.name)
    if (scope.id != null) builder.append("-").append(scope.id)
    val localeString = locale.toString
    if (localeString != "") builder.append("-").append(localeString)
    builder.append(".html")
    for (pkg <- CMS.templateClasspath.toList) {
      val resource = pkg.replace('.', '/') + "/" + builder.toString
      println("Looking for template: " + resource)
      readTemplate(resource) match {
        case null =>
        case xml => return Some(xml)
      }
    }
    None
  }

  def storeTemplate(name : String, scope : Scope, locale : Locale, xml : Option[NodeSeq]) = {
  }

  def isReadOnly = true

  private def readTemplate(resource : String) : NodeSeq = {
      try {
    	  getClass.getClassLoader.getResourceAsStream(resource) match {
    	    case null => null
            case is => XML.load(is)
    	  }
      } catch {
        case e => println("Error reading resource: " + e); throw e
      }
  }
}