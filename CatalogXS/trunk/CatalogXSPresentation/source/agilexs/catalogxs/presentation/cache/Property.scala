package agilexs.catalogxs.presentation.cache

import agilexs.catalogxs.jpa.{catalog => jpa}

case class Property(catalog : Catalog, property : jpa.Property) {

  def product = _product
  def propertyValue = _propertyValue

  private var _product : Option[Product] = None
  private var _propertyValue : Option[jpa.PropertyValue] = None

  private[cache] def set(propertyValue : jpa.PropertyValue) = {
	_propertyValue = Some(propertyValue)
	this
  }

  private[cache] def set(product : Product) = {
    _product = Some(product)
    this
  }
}

