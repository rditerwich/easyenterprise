package claro.cms.shop

import xml.{Node,Text}
import agilexs.catalogxs.jpa
import claro.cms.XmlBinding

trait ShopBindingHelpers {

  object value {
    def apply(property : => Option[Property]) = new XmlBinding(_ => propertyValue(property))
    def apply(property : => Property) = new XmlBinding2(_ => propertyValue(if (property != null) Some(property) else None))
    class XmlBinding2 (f : Seq[Node] => Seq[Node]) extends XmlBinding(f) {}
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
        case jpa.catalog.PropertyType.Money =>
          ShopUtil.formatMoney(property.pvalue.getMoneyCurrency, property.pvalue.getMoneyValue.doubleValue)
        case _ => Text(property.valueAsString)
      }
      case None => Text("")
    }    
  }
}

