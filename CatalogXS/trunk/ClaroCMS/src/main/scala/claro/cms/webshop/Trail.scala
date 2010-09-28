package claro.cms.webshop

import xml.NodeSeq
import net.liftweb.http.{RequestVar, SessionVar}
import claro.cms.{Bindable, Bindings}

private object TrailSession extends SessionVar[TrailSession](new TrailSession)

class TrailSession {
	private var _trail = Seq[Item]()
	private var currentCategoryName : Option[String] = None
	
	def trail = {
	  if (currentCategoryName != WebshopModel.currentCategoryVar.is) {
	  	currentCategoryName = WebshopModel.currentCategoryVar.is
	  	_trail = WebshopModel.currentCategory match {
	  		case Some(category) => determineCurrent(category, _trail.reverse.toList).reverse.toList
	  		case None => Seq.empty 
	  	}
	  }
		_trail
	}		
	
	def determineCurrent(item : Item, reverseTrail : List[Item]) : List[Item] = {
		reverseTrail match {
			case head :: rest if head.children.contains(item) =>  item :: reverseTrail
			case head :: rest => determineCurrent(item, rest)
			case Nil => List(item)
		}
	}
}

object Trail extends RequestVar[Trail](new Trail)

class Trail {

  val shop = WebshopModel.shop
  val trail = TrailSession.trail

	def isOnTrail(item : Item) = trail.contains(item)
	def isSelected(item : Item) = !trail.isEmpty && trail.last == item
	def firstOnTrail : Option[Item] = trail.firstOption
	def parentOnTrail(item : Item) : Option[Item] = trail.takeWhile(_ != item).lastOption
}
