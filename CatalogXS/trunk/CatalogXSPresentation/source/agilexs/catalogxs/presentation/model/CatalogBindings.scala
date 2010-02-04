package agilexs.catalogxs.presentation.model

import net.liftweb.http.S 
import net.liftweb.util._
import net.liftweb.util.Helpers._
import scala.xml.{NodeSeq, Text, SpecialNode} 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.presentation.util.Util

object CatalogBindings {

    def promotionBindings(p: Promotion, tag: Tag, xml: NodeSeq) : NodeSeq = {
	  p match {
        case p : VolumeDiscountPromotion =>
        	bind2(tag or "promotion", xml,           
              "id" -> Text(p.getId.toString),
              "start_date" -> Text(Util.slashDate.format(p.getStartDate)),
              "end_date" -> Text(Util.slashDate.format(p.getStartDate)),
              "price" -> Text(p.getPrice.toString),
              "currency" -> Text(p.getPriceCurrency.toString),
              "volume_discount" -> Text(p.getVolumeDiscount.toString),
              "product" -> (productBindings(p.getProduct, DefaultTag, _)))
        case _ => Seq.empty 
      }
  }
    
  private def bind2(defaultTag: String, xml: NodeSeq, params: BindParam*): NodeSeq = {
    bind(defaultTag, xml, params:_*)
  }
    
  def productBindings(p: Product, tag: Tag, xml: NodeSeq) : NodeSeq = {
    bind2(tag or "product", xml, 
    	"id" -> Text(p.getId.toString),
    	"properties" -> (listPropertyValueBindings(p.getPropertyValues, DefaultTag, _)))
  }
  
  def listPropertyValueBindings(values: Seq[PropertyValue], tag: Tag, xml: NodeSeq) : NodeSeq = {
    values flatMap (propertyValueBindings(_, tag, xml))
  }
  
  def propertyValueBindings(p: PropertyValue, tag: Tag, xml: NodeSeq) : NodeSeq = {
    bind(tag or "property", xml,
         "id" -> Text(p.getId.toString),
         "name" -> Text(p.getProperty.getName),
         "label" -> Text(p.getProperty.getName),
         "value" -> Text(p.getStringValue)
    )
  }
}
