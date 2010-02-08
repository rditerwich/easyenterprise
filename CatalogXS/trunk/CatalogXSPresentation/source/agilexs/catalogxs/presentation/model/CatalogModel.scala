package agilexs.catalogxs.presentation.model

import java.util.LinkedHashSet
import scala.xml.NodeSeq 

import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model.Conversions._

object CatalogModel {
 
  private def cache = Model.catalogCache

  def promotions = cache.promotions
  
  def productGroups(product : Product) : Seq[ProductGroupProduct] = {
    Seq(ProductGroupProduct(null, product))
  }
  
  def propertyValues(product : Product, group : ProductGroup, multiLing : Boolean) = {
  }
  
  def template(obj : Object, template : String) : Option[NodeSeq] = {
    cache.templateObjectCache.get(obj, template) match {
      case Some(xml) => Some(xml)
      case None => cache.templateClassCache.get((obj.getClass, template)) match {
	      case Some(xml) => Some(xml)
	      case None => None
	    }
    }
  }  
}

case class RichProperty(val property : Property, val value : PropertyValue) {
}

class RichProductGroup(val productGroup : ProductGroup, val product : Product) extends Tuple2[ProductGroup, Product](productGroup, product) {
  def products = Model.catalogCache.productGroupProducts(productGroup)
}
