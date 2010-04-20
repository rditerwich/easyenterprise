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
  
  def add(productPrefix : String) : NodeSeq => NodeSeq = xml => {
    findBoundObject(productPrefix) match {
      case Some(product:Product) =>
        val redraws = CurrentRedraws.get
        SHtml.a(() => addProduct(product, redraws), xml) % currentAttributes(Set("product"))
      case None => 
        NodeSeq.Empty
    }
  }

  def clear : NodeSeq => NodeSeq = xml => {
    val redraws = CurrentRedraws.get
    SHtml.a(() => clear(redraws), xml) % current.attributes
  }

  def volume(productOrder : ProductOrder) = { 
    val redraws = CurrentRedraws.get
    SHtml.ajaxText(productOrder.volume.toString,
      updateVolume(productOrder, _, redraws)) % current.attributes
  }
  
  private def addProduct(product : Product, redraws : Redraws) : JsCmd = {
    S.notice("Product added to shopping cart")
    order.addProduct(product, 1)
    redraws.toJsCmd
  }
  
  private def clear(redraws : Redraws) : JsCmd = {
      S.notice("Shopping cart cleared")
      order.clear
      redraws.toJsCmd
  }
  
  private def updateVolume(productOrder : ProductOrder, volume : String, redraws : Redraws) = {
    redraws.toJsCmd
  }
}