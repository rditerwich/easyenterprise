package claro.cms.webshop

import xml.{Elem,Node,NodeSeq,Text,UnprefixedAttribute,TopScope}
import claro.jpa
import claro.cms.{XmlBinding,BindingHelpers}
import claro.cms.jscmds.ReloadPage
import claro.common.util.Conversions._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.{JsCmds}

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

  def logoutLink : NodeSeq => NodeSeq = { xml =>
    SHtml.a(() => {
      WebshopModel.currentUserVar.set(None)
      ReloadPage()
    }, xml) % currentAttributes()
  }
  
  def format(money : Money) = formatMoney(money.amount, money.currency)
  
  def formatMoney(amount : Double, currency : String) = {
    val sign = currency match {
        case "EUR" => <span class="money-sign">&euro;</span>
        case "GBP" => <span class="money-sign">£</span>
        case "USD" => <span class="money-sign">$</span>
        case _ => <span class="money-sign">&euro;</span>
      }  

    val whole : Int = (amount / 100.0).asInstanceOf[Int]
    val cents : Int = (amount / 100.0 - whole).asInstanceOf[Int]
    <span class="money">
      {sign } 
      <span class="money-space"></span>  
      <span class="money-whole">{String.format("%d", int2Integer(whole))}</span>  
      <span class="money-sep">,</span> 
      <span class="money-cents">{String.format("%02d", int2Integer(cents))}</span>
    </span>
  } 
  
  private def propertyValue(property : Option[Property]) = {
    property match { 
      case Some(property) => property.propertyType match {
        case jpa.catalog.PropertyType.Media => 
          if (property.mediaValue == null) 
            <img src={"/images/image-"+property.valueId+".jpg"} /> % currentAttributes()
          else if (property.mimeType == "application/x-shockwave-flash") 
            <object type="application/x-shockwave-flash" data={"/catalog/media/" + property.valueId}/> 
          else if (property.mimeType == "application/pdf")
            <a href={"/catalog/media/" + property.valueId}><img style="padding-bottom:-8px" src="/images/pdf.gif"/></a>
	        else if (property.mimeType.startsWith("image/")) 
	          <img src={"/qqcatalog/media/" + property.valueId} /> % currentAttributes()
	         else
  		       Text(property.mediaValue.toString());
        case jpa.catalog.PropertyType.Money if (property.value != null && property.value.getMoneyValue != null) =>
            formatMoney(property.value.getMoneyValue.getOrElse(0), property.value.getMoneyCurrency)
        case _ => Text(property.valueAsString)
      }
      case None => Text("")
    }    
  }
}

