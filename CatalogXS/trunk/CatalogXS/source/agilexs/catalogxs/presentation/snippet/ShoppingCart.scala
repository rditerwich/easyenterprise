package agilexs.catalogxs.presentation.snippet

import net.liftweb._
import http._
import SHtml._
import S._

import js._
import js.jquery.JqJsCmds._
import js.JE._
import JsCmds._

import mapper._

import util._
import Helpers._

import scala.xml.{NodeSeq, Text}

import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean
import agilexs.catalogxs.jpa.{order => jpa}
import agilexs.catalogxs.presentation.util.Util

//TODO correctly calculate promotion prices, currently the original product price is calculated.
//TODO add button "next step"
//TODO on shoppingcart page, when emptying shopping cart, table should be refreshed too.
//FIXME anchor for buttons no are anchor, with # this introduces new history token, or return false or no a, and set style via css, like cursor

//FIXE "add to" cart on promotion page broken, because "currentProduct" not set when rendering
class ShoppingCart {
  //Session variable that contains the items in the shopping cart during session
  object shoppingCart extends SessionVar[Order](new Order(new jpa.Order))

  /**
   * Method to bind to order_status template to display total number of
   * articles in shopping cart
   */
  def orderStatus (xhtml : NodeSeq) = {
    def inner() : NodeSeq = {
      bind("os", xhtml,
         "empty" ->
           {node : NodeSeq => <a href="#" style="text-decoration:none">{node}</a> %
               ("onclick" -> ajaxInvoke(emptyShoppingBasket _ )._2)},
         "total" -> Text(if (shoppingCart.isEmpty) "0" else shoppingCart.totalProducts.toString))
    }
    inner()
  }

  /**
   * Method to determine the phase the customer is in the shopping process.
   * This method is used to display the wanted html. Use as follows in template:
   * <pre>
   *   <lift:ShoppingCart.show phase="1">
   *     ... code for specific phase goes here
   *   </lift:ShoppingCart.show>
   * </pre>
   *
   */
  def show(xhtml: NodeSeq) : NodeSeq = {
    S.attr("phase").open_! match {
      case "1" => phase1(xhtml)
      case _ => NodeSeq.Empty
    }
  }

  /**
   * Method to be used in template to insert add to cart. The inner
   * xml will be wrapped by the anchor.
   * Use:
   * <pre>
   *   <lift:ShoppingCart.addToCart>
   *     ... html displaying the add button
   *   </lift:ShoppingCart.addToCart>
   * </pre>
   */
  def addToCart(xhtml: NodeSeq) : NodeSeq = {
    val p = Model.currentProduct getOrNull

    def addProduct : JsCmd = {
      shoppingCart.addProduct(p, 1)
      S.notice("Added product " + p.propertiesByName("ArticleNumber").pvalue.getStringValue +
                " to the shopping cart")
      redrawOrderStatus()
    }

    def inner() : NodeSeq = {
      <a href="#" style="text-decoration:none">{xhtml}</a> %
        ("onclick" -> ajaxInvoke(addProduct _ )._2
           /*FIXME: javascript should call: return false = JsExp("return false;")*/)
    }
    inner()
  }

  /*
   * Empties the shopping cart, displays a message and redraws the shopping cart
   * overview on the right side of the page.
   */
  private def emptyShoppingBasket : JsCmd = {
    shoppingCart.empty
    S.notice("Shopping cart emptied")
    redrawOrderStatus()
  }

  /**
   * Redraws the shopping cart on the right side of the page. The template shows
   * the number of items in the shopping cart.
   */
  private def redrawOrderStatus() : JsCmd = {
    SetHtml("order_status",
       TemplateFinder.findAnyTemplate(List("templates-hidden", "order_status")) openOr NodeSeq.Empty)
  }

  /**
   * Within the show phase template 2 templates options are available. One in
   * case no items are in the shopping cart <code>empty</code> and one if there
   * are <code>items</code>. For example:
   * <pre>
   *   <show:empty>
   *     <p>No products in your shopping cart.</p>
   *   </show:empty>
   *   <show:items>
   *     ...
   *   </show:items>
   * </pre>
   */
  private def phase1(xhtml : NodeSeq) : NodeSeq = {
    val id = S.attr("shopping_list_id").open_!

    def inner() : NodeSeq = {
      def reDraw() = SetHtml(id, inner()) & redrawOrderStatus()

      if (shoppingCart.isEmpty) chooseTemplate("show", "no_items", xhtml)
      else
        bind("show", xhtml,
             "no_items" -> NodeSeq.Empty,
             "items" -> {items:NodeSeq => bind("item", items,
                 "list" -> doList(reDraw) _,
                 "totalPrice" -> Util.formatMoney("EUR", shoppingCart.totalPrice))})

    }
    inner()
  }

  private def doList(reDraw: () => JsCmd)(xhtml : NodeSeq) : NodeSeq = {
    def removeProduct(productOrder : jpa.ProductOrder)() : JsCmd = {
      shoppingCart.removeProductOrder(productOrder)
      S.notice(Model.webShop.productsById(productOrder.getProduct().getId().longValue()).propertiesByName("ArticleNumber").pvalue.getStringValue +
               " removed from the shopping cart")
      reDraw()
    }

    def updateVolume(productOrder : jpa.ProductOrder, v : String)() : JsCmd = {
      if (shoppingCart.updateVolume(productOrder, Integer.valueOf(v).intValue)) {
        S.notice("Volume of " +
                Model.webShop.productsById(productOrder.getProduct().getId().longValue()).propertiesByName("ArticleNumber").pvalue.getStringValue +
                " updated to " + v)
      }
      reDraw()
    }

    shoppingCart.delegate.getProductOrders.toSeq.flatMap(productOrder => {
      bind("shoppingcart", xhtml,
          "deleteButton" -> {node : NodeSeq => <a href="#" style="text-decoration:none">{node}</a> %
             ("onclick" -> ajaxInvoke(removeProduct(productOrder) _)._2)},
          "product" ->
            CatalogBindings.productBinding(
              Model.webShop.productsById(productOrder.getProduct().getId().longValue())).bind("product", chooseTemplate("shoppingcart", "product", xhtml)),
          "volume" -> ajaxText(productOrder.getVolume.toString,
              v => updateVolume(productOrder, v)),
          "priceTotal" -> Util.formatMoney(
             Model.webShop.productsById(
               productOrder.getProduct().getId().longValue()).propertiesByName("Price").pvalue.getMoneyCurrency,
             productOrder.getPrice.doubleValue))
    })
  }
}
