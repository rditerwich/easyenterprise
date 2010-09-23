package claro.cms.webshop

import scala.collection.SortedSet
import net.liftweb.http.js.{JsCmd,JsCmds}
import net.liftweb.http.{SessionVar, SHtml}
import xml.{Node, NodeSeq}
import claro.cms.{Binding, Bindable, BindingHelpers, BindingContext, CurrentRedraws, Redrawable}
import claro.cms.jscmds.ReloadPage

object Filtering extends SessionVar[Filtering](new Filtering) 

class Filtering extends Bindable with Redrawable {
	val defaultPrefix = "filtering"
	var filters = SortedSet[Filter]()
	
	// skip content when filtering is not relevant
	override def bind(node : Node, context : BindingContext) = super.bind(node, context)

  override lazy val bindings = Map(
  	"filters" -> filters,
  	"clear-link" -> clearLink)

  def hasCategoryFilter(category : Category) = filters.contains(CategoryFilter(category))
		
	def addFilterLink(category: Category) = (xml: NodeSeq) => {
    def callback = {
    	val redraws = CurrentRedraws.get
    	filters += CategoryFilter(category)
      redraws.toJsCmd
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }
	
	def clearLink = (xml: NodeSeq) => {
		def callback = {
				filters = SortedSet.empty
				ReloadPage()
		}
		SHtml.a(() => callback, xml) % currentAttributes()
	}
}

trait Filter extends Bindable with Ordered[Filter] with BindingHelpers {
	val defaultPrefix = "filter"
  val title : String
  def products : Set[Product]
  
  override lazy val bindings = Map(
 		"title" -> title,
  	"remove" -> removeLink)
  
  def removeLink = (xml : NodeSeq) => {
    def callback = {
    	Filtering.filters -= this
      JsCmds.Noop
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }
  def compare(that: Filter) = title.compare(that.title)
}

case class CategoryFilter(category : Category) extends Filter {
  val title : String = "Category " + category.name
  override def products = category.productExtent 
}
