package agilexs.catalogxs.presentation.snippet

import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
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

//TODO format money
//TODO show correct currency sign
//TODO how to handle multiple currencies
class ShoppingCart {
  //Session variable that contains the items in the shopping cart during session
  object shoppingCart extends SessionVar[jpa.Order](new jpa.Order)
  //object orderStatusID extends SessionVar[Box[Long]](Empty)

  /**
   * Method to bind to order_status template to display total number of 
   * articles in shopping cart
   */
  def orderStatus (xhtml : NodeSeq) = {
    val id = S.attr("order_status_id").open_!

    def reDraw() = SetHtml(id, inner())

    def inner() : NodeSeq = {
      bind("os", xhtml, 
         "total" -> Text(  
           if (shoppingCart.getProductOrders == null || 
               shoppingCart.getProductOrders.isEmpty) "0" else 
             totalArticles.toString))
    }
    inner()
  }

  def redrawOrderStatus() : JsCmd = {
    //SetHtml(orderStatusID, inner)
    null;
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
    val phase = S.attr("phase").open_!

    phase match {
      case "1" => phase1(xhtml)
      case _ => NodeSeq.Empty
    }
  }

  def add(xhtml: NodeSeq) = {
    val p = Model.currentProduct orNull
    val o = new jpa.ProductOrder()
    o.setProduct(p.delegate)
    o.setVolume(1)

    def inner(reDraw: () => JsCmd)(xhtml: NodeSeq) : NodeSeq = {
      //val price = p.propertiesByName.get("Price")
      //o.setPrice(price.toLong)
      bind("sc", xhtml,
           "add" -> ajaxButton(Text("Add to Cart"), () => {
                  shoppingCart.getProductOrders.add(o);reDraw()}))
    }
    inner(redrawOrderStatus)(xhtml)
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
  def phase1(xhtml : NodeSeq) : NodeSeq = {
    val id = S.attr("shopping_list_id").open_!

    //FIXME: remove test code from release
    val po = new jpa.ProductOrder
    po.setVolume(10)
    po.setPrice(10 *
       Model.catalog.productsById(5001).propertiesByName("Price").valueAsString.toDouble)
    po.setProduct(Model.catalog.productsById(5001).delegate)
    shoppingCart.getProductOrders.add(po)

    def inner(): NodeSeq = {
      def reDraw() = SetHtml(id, inner())
 
      if (shoppingCart.getProductOrders == null || 
           shoppingCart.getProductOrders.isEmpty)
        chooseTemplate("show", "empty", xhtml)
      else {
           val nhtml = chooseTemplate("show", "items", xhtml);
           bind("item", nhtml,
                "list" -> list(reDraw)(chooseTemplate("item", "list", nhtml)),
                "totalPrice" -> <b>&euro; {totalPrice}</b>)
      }
    }
    inner()
  }

  def list(reDraw: () => JsCmd)(xhtml : NodeSeq) : NodeSeq = {
    shoppingCart.getProductOrders.flatMap(productOrder => {
      bind("shoppingcart", xhtml,
          "deleteButton" -> 
            ajaxButton(Text("delete"),
              () => {shoppingCart.getProductOrders.remove(productOrder); 
               reDraw()}),
          "product" -> 
            CatalogBindings.productBinding(
              Model.catalog.productsById(productOrder.getProduct().getId().longValue())).bind("product", chooseTemplate("shoppingcart", "product", xhtml)),
          "volume" -> SHtml.text(
              productOrder.getVolume.toString, 
              { v => productOrder.setVolume(Integer.valueOf(v))},
              "maxlength" -> "3", "style" -> "width:30px;margin:0 5px;"),
          "priceTotal" -> <span>&euro; {productOrder.getPrice}</span>)}) 
  }

  private def totalArticles : Int = {
    (0 /: shoppingCart.getProductOrders.map(_.getVolume.intValue)) (_ + _) 
  }

  private def totalPrice : Double = {
    (0.0 /: shoppingCart.getProductOrders.map(_.getPrice.doubleValue)) (_ + _) 
  }
}
