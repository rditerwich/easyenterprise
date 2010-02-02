package agilexs.catalogxs.presentation.model

import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text, SpecialNode} 
import agilexs.catalogxs.jpa.catalog._

object CatalogBindings {

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
    	"id" -> Text(p.getId.toString),
    	"properties" -> (xhtml => propertyValueBindings(null)(xhtml)))
  }
  
  def propertyValueBindings(p: PropertyValue)(xhtml: NodeSeq) : NodeSeq = {
    bind("property", xhtml,
         "id" -> Text(p.getId.toString),
         "name" -> Text(p.getProperty.getName),
         "label" -> Text(p.getProperty.getName)
    )
  }

}
