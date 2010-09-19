package claro.cms.webshop

import xml.NodeSeq
import net.liftweb.http.SessionVar
import claro.cms.{Bindable, Bindings}

/**
 * Navigation retains user's current crumb-trail, current navigation tree 
 * inside the category graph.
 * 
 * Navigation is retained in user's session.
 * @author ruud
 */
object Navigation extends SessionVar[RootNavigation](new RootNavigation)

trait Navigation extends Bindable {

	override val defaultPrefix = "navigation"
  val root : RootNavigation
  val parent : Option[Navigation]
	val category : Category = null
	def name : String = ""
  
  def isCurrent : Boolean = isSelected || (parent match {
	  case Some(navigation) => navigation.isCurrent
	  case None => false
  })
  
  def children : Seq[Seq[Navigation]]
  
  def isSelected = root.selected.isDefined && root.selected.get == this
	
  def select = root.selected = Some(this)
  
  val currentCategory : Option[Category] = None
  
  def link : NodeSeq => NodeSeq
  
  override lazy val bindings = Map(
    "is-current" -> isCurrent,
	  "is-selected" -> isSelected,
	  "link" -> link,
	  "name" -> name,
	  "test" -> "TEST",
	  "children" -> children
  )
}

class RootNavigation extends Navigation {
  val root = this
  val parent = None
  var selected : Option[Navigation] = None
  def link = Link.home
  lazy val children = {
    val shop = WebshopModel.shop
    shop.cacheData.topLevelNavigation.map(_.map(n => new CategoryNavigation(this, Some(this), shop.mapping.categories(n.getCategory))))
  }
}

class CategoryNavigation(val root : RootNavigation, val parent : Option[Navigation], override val category : Category) extends Navigation {
	lazy val children = Seq(category.children map (new CategoryNavigation(root, Some(this), _)))
	override def name = category.name
	def link = Link(category)
}
