package claro.cms.webshop.data

import claro.cms.Cms
import claro.jpa.catalog._
import scala.collection.JavaConversions._

trait WebshopDataFiller {
  val em = Cms.entityManager

  def property(name : String, tpe : PropertyType) = {
    val query = em.createQuery("select from Property p where p.name = :name").setParameter("name", name)
    val result = query.getResultList.asInstanceOf[java.util.List[Property]]
    val property = if (result.isEmpty) new Property else result.get(0)
    property.getLabels.add(label(name))
    property.setType(tpe)
    property
  }
  
  def set(item : Item, property : Property, value : Any, language : String = null) = {
    val value = item.getPropertyValues.find(v => v.getProperty == property && v.getLanguage == language).getOrElse(new PropertyValue)
    value.setProperty(property)
    value.setLanguage(language)
    property.getType match {
      case PropertyType.String => value.setStringValue(value.asInstanceOf[String])
      case PropertyType.Money => value.setMoneyValue(value.asInstanceOf[Double])
    }
  }
  
  def label(name : String, language : String = null) = {
    val label = new Label
    label.setLanguage(language)
    label.setLabel(name)
    label
  }

}