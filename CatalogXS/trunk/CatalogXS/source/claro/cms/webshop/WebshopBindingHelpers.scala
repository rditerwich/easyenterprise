package claro.cms.webshop

import xml.{Node,NodeSeq,Text}
import agilexs.catalogxs.jpa
import claro.cms.XmlBinding

trait WebshopBindingHelpers {

  object value {
    def apply(property : => Option[Property]) = new XmlBinding(_ => propertyValue(property))
    def apply(property : => Property) = new XmlBinding2(_ => propertyValue(if (property != null) Some(property) else None))
    class XmlBinding2 (f : NodeSeq => NodeSeq) extends XmlBinding(f) {}
  }

  private def propertyValue(property : Option[Property]) = {
    property match { 
      case Some(property) => property.propertyType match {
        case jpa.catalog.PropertyType.Media => 
          if (property.mediaValue == null) 
            <img src={"/images/image-"+property.valueId+".jpg"} />
	        else if (property.mimeType.startsWith("image/")) 
		      <img src={"image/" + property.valueId} />
	         else
  		       Text(property.mediaValue.toString());
        case jpa.catalog.PropertyType.Money if (property.value != null && property.value.getMoneyValue != null) =>
            WebshopUtil.formatMoney(property.value.getMoneyCurrency, property.value.getMoneyValue.doubleValue)
        case _ => Text(property.valueAsString)
      }
      case None => Text("")
    }    
  }
}

