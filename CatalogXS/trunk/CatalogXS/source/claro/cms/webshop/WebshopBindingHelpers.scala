package claro.cms.webshop

import xml.{Elem,Node,NodeSeq,Text,UnprefixedAttribute,TopScope}
import agilexs.catalogxs.jpa
import claro.cms.{XmlBinding,BindingHelpers}
import claro.common.util.Conversions._

trait WebshopBindingHelpers extends BindingHelpers {

  object value {
    def apply(property : => Option[Property]) = new XmlBinding(_ => propertyValue(property))
    def apply(property : => Property) = new XmlBinding2(_ => propertyValue(if (property != null) Some(property) else None))
    class XmlBinding2 (f : NodeSeq => NodeSeq) extends XmlBinding(f) {}
  }
  
  def searchAllLink : NodeSeq => NodeSeq = { xml => 
    WebshopModel.currentSearchStringVar.is match {
      case Some(s) => new Elem(null, "a", new UnprefixedAttribute("href", "/search/" + s, current.attributes), TopScope, xml:_*);
      case None => NodeSeq.Empty
    }
  }

  def money(amount : Double, currency : String) = {
    //Unparsed doesn't seem to work in combination with ajax calls...
    //Therefore we use the symbol tokens instead of html symbols.
    Text(currency match {
        case "EUR" => "€"; //"&euro;"
        case "GBP" => "£"; //"&pound;";
        case "USD" => "$";
        case _ => "&euro;";
    }) ++
    Text(" ") ++
    Text(String.format("%.2f", double2Double(amount / 100.0)))
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
            money(property.value.getMoneyValue.getOrElse(0), property.value.getMoneyCurrency)
        case _ => Text(property.valueAsString)
      }
      case None => Text("")
    }    
  }
}

