package claro.cms.webshop

import xml.NodeSeq
import net.liftweb.http.{RequestVar, SessionVar}
import claro.cms.{Bindable, Bindings}

private object TrailSession extends SessionVar[TrailSession](new TrailSession)

class TrailSession {
	private var _trail = collection.mutable.Seq[Item]()
	private var currentCategoryName : Option[Int] = None
	
	def trail = {
	  if (currentCategoryName != WebshopModel.currentCategoryVar.is) {
	  }
		_trail
	}		
}

object Trail extends RequestVar[Trail](new Trail)

class Trail {

  val shop = WebshopModel.shop
  val trail = TrailSession.trail

	def isOnTrail(item : Item) = trail.contains(item)
	def isSelected(item : Item) = !trail.isEmpty && trail.last == item
	def firstOnTrail : Option[Item] = trail.firstOption
}
