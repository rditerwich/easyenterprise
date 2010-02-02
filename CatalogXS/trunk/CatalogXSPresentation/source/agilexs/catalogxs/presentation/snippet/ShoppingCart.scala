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

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean

class ShoppingCart {
  /**
   * Method to determine the phase the customer is in the shopping process. This method
   * is used to display the wanted html. Use as follows in template:
   * <pre>
   *   <lift:ShoppingCart.phase phase="1">
   *     ... code for specific phase goes here 
   *   </lift:ShoppingCart.phase>
   * </pre>
   */
  def phase(html: NodeSeq) : NodeSeq = {
    val phase = S.attr("phase").open_!
    
    if (phase.equals(S.param("shoppingCart").openOr("1"))) html else NodeSeq.Empty
  }
  
  def itemsInBasket(xhtml : NodeSeq) : NodeSeq = {
    xhtml
  }
  def total (xhtml : NodeSeq) : NodeSeq = {
    xhtml
  }

  def list (xhtml : NodeSeq) : NodeSeq = {
/*    
    catalogs.flatMap(catalog =>
      bind("shoppingcart", xhtml,
          "deleteButton" -> Text("del"),
          "amount" -> Text(""),
          "product" -> Text(""),
          "pricePerProduct" -> Text(""),
          "priceTotal" -> Text(""),
          "inStock" -> Text("")))
*/
    xhtml
  }
  
}
