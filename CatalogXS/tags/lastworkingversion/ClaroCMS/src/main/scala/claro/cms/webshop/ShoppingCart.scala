package claro.cms.webshop

import claro.jpa
import claro.cms.{Bindable, Redrawable, CurrentRedraws}
import claro.common.util.Conversions._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmds
import scala.xml.{NodeSeq, Text}

//TODO correctly calculate promotion prices, currently the original product price is calculated.
//TODO add button "next step"
//TODO on shoppingcart page, when emptying shopping cart, table should be refreshed too.
//FIXME anchor for buttons no are anchor, with # this introduces new history token, or return false or no a, and set style via css, like cursor

object ShoppingCart extends ShoppingCart {}

class ShoppingCart private extends Bindable with Redrawable {

  override def bindings = bindingsFor(this)
  
  def order = WebshopModel.currentOrder.get

  def addProduct(productPrefix : String) : NodeSeq => NodeSeq = {
    findBoundObject(productPrefix) match {
      case Some(product : Product) =>
      	addProduct(product)
      case None => 
        _ => NodeSeq.Empty
    }
  }
  
  def addProduct(product : Product) : NodeSeq => NodeSeq = xml => {
	  val redraws = CurrentRedraws.get
	  def callback = {
		  order.addProduct(product, 1)
		  S.notice("Product added to shopping cart")
		  redraws.toJsCmd
	  }
	  SHtml.a(() => callback, xml) % currentAttributes("product-prefix")
  }

  def shippingCosts = Money(15, "EUR")
  
  def proceedOrderLink = (xml : NodeSeq) => 
    <a href="/shipping">{xml}</a> % currentAttributes()
  
  def placeOrderLink = (xml : NodeSeq) => {
    val redraws = CurrentRedraws.get
    def callback = {
      WebshopDao.transaction { em =>
        WebshopModel.currentUserVar.get match {
          case Some(user) => WebshopDao.findUserById(user.getId getOrElse(-1)) match {
            case Some(user) => 
              val transport = new jpa.shop.Transport
              transport.setDesciption("Standard Delivery")
              transport.setDeliveryTime(14)
              transport.setTransportCompany("UPS")
              
              order.order.setUser(user)
              order.order.setOrderDate(new java.util.Date())
              order.order.setAmountPaid(0d)
              order.order.setStatus(jpa.shop.OrderStatus.PendingPayment)
              order.order.setTransport(transport)
              em.merge(order.order)
              
              // clear shopping basket
              WebshopModel.currentOrder.remove()
              S.notice("Order has been placed")
            case None =>
          }
          case None =>
        }
        redraws.toJsCmd
      }
    }
    SHtml.a(() => callback, xml) % currentAttributes()
  }

  def addPromotion(promotionPrefix : String) : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    def callback(promotion : Promotion) = {
      promotion match {
        case p : VolumeDiscountPromotion =>
          S.notice("Promotion added to shopping cart")
          order.addProduct(p.product, p.volumeDiscount)
      }
      redraws.toJsCmd
    }
    findBoundObject(promotionPrefix) match {
      case Some(promotion:Promotion) =>
        SHtml.a(() => callback(promotion), xml) % currentAttributes("promotion-prefix")
      case None => 
        NodeSeq.Empty
    }
  }
  
  def clear : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    def callback = {
      S.notice("Shopping cart cleared")
      order.clear
      redraws.toJsCmd
    }
    SHtml.a(() => callback, xml) % current.attributes
  }

  def updateVolume(productOrder : ProductOrder) = { 
    val redraws = CurrentRedraws.get
    def callback(volume : String) = {
      productOrder.volume = volume.toIntOr(productOrder.volume)
      redraws.toJsCmd
    }
    SHtml.ajaxText(productOrder.volume.toString, callback _) % current.attributes
  }
  
  def removeProductOrder(productOrder : ProductOrder) = (xml : NodeSeq) => { 
    val redraws = CurrentRedraws.get
    def callback = {
        productOrder.remove
        redraws.toJsCmd
    }
    SHtml.a(() => callback, xml) % current.attributes
  }
  
  
}