package claro.cms.webshop

import net.liftweb.http.SessionVar

/**
 * Navigation retains user's current crumb-trail, current navigation tree 
 * inside the category graph.
 * 
 * Navigation is retained in user's session.
 * @author ruud
 */
object Navigation extends SessionVar[Navigation](null)

trait Navigation {

  val currentCategory : Option[Category] = None
  
  lazy val navigation = WebshopModel.shop.cacheData.topLevelNavigation 
  
  def name : String
  
  def isSelected(category : Category)
  
  def currentSubNavigation : Option[Navigation]
  
  def categories : Seq[Seq[Navigation]]
  
  val mappings = Map(
      
  )
}

case class NavigationSelection