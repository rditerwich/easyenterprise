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
	  		case None => WebshopModel.currentProduct match {
		  		case Some(product) => determineCurrent(product, _trail.reverse.toList).reverse.toList
		  		case None => Seq.empty 
		  	} 
	  	}
	  }
		_trail
	}		
	
	def determineCurrent(item : Item, reverseTrail : List[Item]) : List[Item] = {
		reverseTrail match {
			case (head : Product) :: rest if head.children.contains(item) => item :: reverseTrail
			case (head : Category) :: rest if isChildOf(item, head) =>  item :: reverseTrail
			case head :: rest => determineCurrent(item, rest)
			case Nil => List(item)
		}
	}
	
	def isChildOf(item : Item, category : Category) = item match {
		case product : Product => category.productExtent.contains(product)
		case item : Item => category.children.contains(item)
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
