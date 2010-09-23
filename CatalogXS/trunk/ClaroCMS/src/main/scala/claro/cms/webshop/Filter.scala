package claro.cms.webshop

import scala.collection.SortedSet
import net.liftweb.http.js.{JsCmd,JsCmds}
import net.liftweb.http.{SessionVar, SHtml}
import xml.NodeSeq
import claro.cms.BindingHelpers

object Filter extends SessionVar[SortedSet[Filter]](SortedSet()) with BindingHelpers {
  def addFilterLink(category: Category) = (xml: NodeSeq) => {
    def callback = {
    	println("CALLBAK")
    	set(get + CategoryFilter(category))
      JsCmds.Noop
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }
}

trait Filter extends Ordered[Filter] with BindingHelpers {
  val title : String
  def items : Seq[Item]
  def removeLink = (xml : NodeSeq) => {
    def callback = {
    	Filter.set(Filter.get - this)
      JsCmds.Noop
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }
  def compare(that: Filter) = title.compare(that.title)
}

case class CategoryFilter(group : Category) extends Filter {
  val title : String = "Category " + group.name
  override def items = group.children
}
