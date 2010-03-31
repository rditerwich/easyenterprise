package claro.cms

import scala.xml.{Node,NodeSeq,XML,Text}
import scala.collection.{mutable}
import java.io.{File,FileInputStream}
import java.util.Locale
import java.util.concurrent.{ConcurrentHashMap,TimeUnit}
import claro.cms.util.ParseHtml
import claro.common.util.Locales
import claro.common.util.Conversions._
import Conversions._

object Template {
  def apply(name : String) = new Template(name, null)
}

case class Template(name : String, obj : Any) {
}

case class ConcreteTemplate(resource : Resource, xml : NodeSeq) {

  private def tuples : Seq[(String,NodeSeq)] = xml filter (isNested(_)) map (node => (name(node), node.child))
  private def tuples2 : Seq[(String,NodeSeq)] = tuples ++ Seq(("", contents))
  val nestedTemplates = Map(tuples2:_*)  

  private def contents = xml filter (!isNested(_))
  private def name(node : Node) : String = node.attribute("template") getOrElse NodeSeq.Empty toString
  private def isNested(node : Node) = node.prefix == "template" && node.label == "define"
}

class TemplateCache(site : Site) {

  private val objectTemplateCache = new ConcurrentHashMap[(Template,Locale),Option[ConcreteTemplate]]()

  def apply(template : Template, locale : Locale) : Option[ConcreteTemplate] =
    if (!site.caching) find(template, locale) 
    else objectTemplateCache getOrElseUpdate ((template,locale), find(template, locale))
  
  def find(template : Template, locale : Locale) : Option[ConcreteTemplate] = 
    site.templateStore.find(resourceLocator(template), Locales.getAlternatives(locale)) match {
      case Some(resource) => Some(ConcreteTemplate(resource, resource.readHtml))
      case None => None
    }
  
  private def resourceLocator(template : Template) : ResourceLocator =
    site.templateLocators.findFirst(template).
      getOrElse (ResourceLocator(template.name, "html", Scope()))

  private[cms] def flushCache(templateName : String) = {
    val it = objectTemplateCache.keySet.iterator
    while (it.hasNext) {
      if (it.next._1.name== templateName) {
        it.remove
      }
    }
  }
}

