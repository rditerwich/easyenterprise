package agilexs.catalogxs.presentation.cache

import scala.collection.Set
import scala.collection.mutable

import agilexs.catalogxs.jpa.{catalog => jpa}
import agilexs.catalogxs.presentation.model.Conversions._ 

case class ProductGroup(catalog : Catalog, productGroup : jpa.ProductGroup) {

  def product = _product 
  
  lazy val products : Set[Product] = 
   catalog.productGroupProducts(productGroup)
  
  lazy val productExtent : Set[Product] =
    catalog.productGroupProductExtent(productGroup)
  
  private var _product : Option[Product] = None

  private[cache] def set(product : Product) = {
    _product = Some(product)
    this
  }

}
