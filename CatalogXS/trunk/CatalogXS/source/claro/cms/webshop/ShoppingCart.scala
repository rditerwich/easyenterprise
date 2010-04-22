package claro.cms.webshop

import net.liftweb.http.{RequestVar,SessionVar,S,SHtml}
import net.liftweb.http.js.{JsCmd,JsCmds}
import scala.xml.{Node,NodeSeq, Text}
import agilexs.catalogxs.jpa
import claro.common.util.Conversions._
import claro.cms.Bindable
import collection.mutable
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmd
import javax.servlet.http.HttpServletRequest

//TODO correctly calculate promotion prices, currently the original product price is calculated.
//TODO add button "next step"
//TODO on shoppingcart page, when emptying shopping cart, table should be refreshed too.
//FIXME anchor for buttons no are anchor, with # this introduces new history token, or return false or no a, and set style via css, like cursor

object ShoppingCart extends ShoppingCart {}

class ShoppingCart private extends Bindable with Redrawable {

  object order extends SessionVar[Order](new Order(new jpa.shop.Order, WebshopModel.shop.get.mapping))

  override def bindings = bindingsFor(this)
  
  def addProduct(productPrefix : String) : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    def callback(product : Product) = {
      S.notice("Product added to shopping cart")
      order.addProduct(product, 1)
      redraws.toJsCmd
    }
    findBoundObject(productPrefix) match {
      case Some(product:Product) =>
        SHtml.a(() => callback(product), xml) % currentAttributes(Set("product"))
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