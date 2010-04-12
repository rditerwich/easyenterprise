package claro.cms

import scala.xml.{Node,XML,Text}
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

case class ConcreteTemplate(resource : Resource, xml : Seq[Node]) {

  private def tuples : Seq[(String,Seq[Node])] = xml filter (isNested(_)) map (node => (name(node), node.child))
  private def tuples2 : Seq[(String,Seq[Node])] = tuples ++ Seq(("", contents))
  val nestedTemplates = Map(tuples2:_*)  

  private def contents = xml filter (!isNested(_))
  private def name(node : Node) : String = node.attribute("template") getOrElse Seq.empty toString
  private def isNested(node : Node) = node.prefix == "template" && node.label == "define"
}

class TemplateStore(site : Site, resourceStore : ResourceStore) {

  private val objectTemplateCache = new ConcurrentHashMap[(Template,Locale),Option[ConcreteTemplate]]()

  def apply(template : String, locale : Locale) : Option[ConcreteTemplate] = apply(Template(template), locale)
    
  def apply(template : Template, locale : Locale) : Option[ConcreteTemplate] =
    if (!site.caching) find(template, locale) 
    else objectTemplateCache getOrElseUpdate ((template,locale), find(template, locale))
  
  private def find(template : Template, locale : Locale) : Option[ConcreteTemplate] = 
    resourceStore.find(resourceLocator(template), Locales.getAlternatives(locale)) match {
      case Some(resource) => Some(ConcreteTemplate(resource, resource.readHtml))
      case None => None
    }
  
  private def resourceLocator(template : Template) : ResourceLocator =
    site.templateLocators.findFirst(template).
      getOrElse (ResourceLocator(template.name, "html", List(Scope.global)))

  private[cms] def flushCache(templateName : String) = {
    val it = objectTemplateCache.keySet.iterator
    while (it.hasNext) {
      if (it.next._1.name== templateName) {
        it.remove
      }
    }
  }
}

class TemplateComponent extends Component {
  
  val prefix = "template"
  
  bindings.append {
    case _ : TemplateComponent => Map(
      "include" -> new IncludeBinding(Map.empty))
  }

  class IncludeBinding(currentTemplates : Map[String,Seq[Node]]) extends Binding {
    def bind(node : Node, context : BindingContext) : Seq[Node] = {
      val name = @@("template")
      val (templateNodes,contentNodes) = node.child.partition(node => node.prefix == prefix && node.label == "define")
      val templateMap : Map[String,Seq[Node]] = Map(templateNodes.toSeq map(n => (attr(n, "template"), n.child)):_*)
      val content = contentNodes.toSeq
      val template : Seq[Node] = currentTemplates.get(name) getOrElse (site.templateStore(name, locale) match {
        case Some(template) => template.xml
        case None => content
      })
	  Binding.bind(template, context + (prefix -> Map(
        "include" -> new IncludeBinding(currentTemplates ++ templateMap),
        "content" -> content))) 
    }
  }
}

