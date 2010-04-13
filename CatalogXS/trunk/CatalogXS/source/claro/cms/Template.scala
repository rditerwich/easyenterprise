package claro.cms

import scala.xml.{Node,NodeSeq,XML,Text}
import scala.collection.{mutable}
import java.io.{File,FileInputStream}
import java.util.Locale
import java.util.concurrent.{ConcurrentHashMap,TimeUnit}
import claro.cms.util.ParseHtml
import claro.common.util.Locales
import claro.common.util.Conversions._

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
  private def name(node : Node) : String = node.attribute("template") getOrElse Seq.empty toString
  private def isNested(node : Node) = node.prefix == "template" && node.label == "define"
}

class TemplateCache(val store : TemplateStore) {
  val site = store.site
  private val objectTemplateCache = new ConcurrentHashMap[(Template,Locale),Option[ConcreteTemplate]]()

  def apply(template : String, locale : Locale) : Option[ConcreteTemplate] = apply(Template(template), locale)
    
  def apply(template : Template, locale : Locale) : Option[ConcreteTemplate] =
    if (!site.caching) store.find(template, locale) 
    else objectTemplateCache getOrElseUpdate ((template,locale), store.find(template, locale))

  private[cms] def flush(templateName : String) = {
    val it = objectTemplateCache.keySet.iterator
    while (it.hasNext) {
      if (it.next._1.name== templateName) {
        it.remove
      }
    }
  }
}

class TemplateStore(val site : Site, resourceStore : ResourceStore) {

  def find(template : Template, locale : Locale) : Option[ConcreteTemplate] = 
    resourceStore.find(resourceLocator(template), Locales.getAlternatives(locale)) match {
      case Some(resource) => Some(ConcreteTemplate(resource, resource.readHtml))
      case None => None
    }
  
  private def resourceLocator(template : Template) : ResourceLocator =
    site.templateLocators.findFirst(template).
      getOrElse (ResourceLocator(template.name, "html", List(Scope.global)))
}

class TemplateComponent extends Component {
  
  val prefix = "template"
  
  bindings.append {
    case _ : TemplateComponent => Map(
      "include" -> new IncludeBinding(Map.empty))
  }

  class IncludeBinding(currentTemplates : Map[String,NodeSeq]) extends Binding {
    def bind(node : Node, context : BindingContext) : NodeSeq = {
      val name = @@("template")
      val (templateNodes,contentNodes) = node.child.partition(node => node.prefix == prefix && node.label == "define")
      val templateMap : Map[String,NodeSeq] = Map(templateNodes.toSeq map(n => (attr(n, "template"), n.child)):_*)
      val content : NodeSeq = contentNodes.toSeq
      val template : NodeSeq = currentTemplates.get(name) getOrElse (site.templateCache(name, locale) match {
        case Some(template) => template.xml
        case None => content
      })
	  Binding.bind(template, context + (prefix -> Map(
        "include" -> new IncludeBinding(currentTemplates ++ templateMap),
        "content" -> content))) 
    }
  }
}

