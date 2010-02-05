package agilexs.catalogxs.presentation.model

import Conversions._
import net.liftweb.util.BindHelpers._
import scala.xml.Text 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.util.Util

object CatalogBindings {
  
  def promotionBinding(promotion : Promotion) = promotion match {
  	case p : VolumeDiscountPromotion => volumeDiscountPromotionBinding(p)
  	case _ => Binding()
  }
  
  def volumeDiscountPromotionBinding(promotion : VolumeDiscountPromotion) = Binding(promotion,           
      "id" -> Text(promotion.getId.toString),
      "start_date" -> Text(Util.slashDate.format(promotion.getStartDate)),
      "end_date" -> Text(Util.slashDate.format(promotion.getStartDate)),
      "price" -> Text(promotion.getPrice.toString),
      "currency" -> Text(promotion.getPriceCurrency.toString),
      "volume_discount" -> Text(promotion.getVolumeDiscount.toString),
      "product" -> productBinding(promotion.getProduct) -> "product")

  def volumeDiscountPromotionShowSmall(promotion : VolumeDiscountPromotion) = Binding(
  )
  
  def productBinding(product : Product) = Binding(product,   
	  "id" -> Text(product.getId.toString),
      "properties" -> (product.getPropertyValues map (propertyValueBinding(_))) -> "property")
//	  "name" -> Text(product.getName)
  
   def propertyValueBinding(value: PropertyValue) = Binding(value,  
     "id" -> Text(value.getId.toString),
     "name" -> Text(value.getProperty.getName),
     "label" -> Text(value.getProperty.getName),
     "value" -> Text(value.getStringValue)
   )
}
