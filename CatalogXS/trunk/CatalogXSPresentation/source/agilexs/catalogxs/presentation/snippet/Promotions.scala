package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.{NodeSeq, Text, SpecialNode} 
import scala.util.logging.ConsoleLogger 
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.jpa.catalog._

class Promotions extends BasicSnippet[Promotion] {

  def all(xhtml: NodeSeq) : NodeSeq = {
      val catalogBean = lookupCatalog()
      val promotions = Model.listToWrapper(catalogBean.findAllPromotions(0, 9).asInstanceOf[java.util.List[Promotion]])
      promotions.flatMap(promotionBindings(_)(xhtml))
  }
  
  def promotionBindings(p: Promotion)(xhtml: NodeSeq) : NodeSeq = {
	  p match {
        case p : VolumeDiscountPromotion =>
        	bind("promotion", xhtml,           
              "id" -> Text("VOLDISCPROMO_ID_X"),
              "start-date" -> Text(p.getStartDate.formatted("YYYY")),
              "end-date" -> Text(p.getStartDate.formatted("YYYY/mm/dd")),
              "price" -> Text(p.getPrice.toString),
              "currency" -> Text(p.getPriceCurrency.toString),
              "volume-discount" -> Text(p.getVolumeDiscount.toString),
              "product" -> (xhtml => productBindings(p.getProduct)(xhtml)))
        case _ => Seq.empty 
      }
  }
  
  def productBindings(p: Product)(xhtml: NodeSeq) : NodeSeq = {
    bind("product", xhtml, 
    	"id" -> Text(p.getId.toString))
  }
}  
