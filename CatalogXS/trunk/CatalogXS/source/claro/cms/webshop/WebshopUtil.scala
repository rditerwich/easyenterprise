package claro.cms.webshop

import java.util.Date
import java.text.SimpleDateFormat
import net.liftweb.util.{Bindable,Box,Full,Empty}
import net.liftweb.http.{S}
import scala.xml.{NodeSeq, Node, Text, Unparsed, Elem, UnprefixedAttribute, TopScope} 
import agilexs.catalogxs.jpa

object WebshopUtil {
	
  val noSlashDate = new SimpleDateFormat("yyyyMMdd")
  val slashDate = new SimpleDateFormat("yyyy/MM/dd")
 
  def splitEvery[A](as : List[A], n : Int) : List[List[A]] = as.splitAt(n) match {
    case (a, Nil) => a :: Nil
    case (a, b)   => a :: splitEvery(b, n)
  }
 
  def getIntParam(name : String, default : Int) : Int = {
    try { 
      S.param(name).map(_.toInt) openOr default
    }
    catch {
      case e => default // Should log something in this case
    } 
  }
 
  type DateConverter = String => Date
 
  def parseDate(value : String, converter : DateConverter) : Box[Date] =
    try {
      Full(converter(value))
    } catch {
      case e => Empty
    }  
 
  def getDateParam(name : String, converter : DateConverter) : Box[Date] = {
    S.param(name).map(parseDate(_, converter)) openOr Empty
  }
  
  def formatMoney(currency: String, value : Double) : NodeSeq  = {
    //Unparsed doesn't seem to work in combination with ajax calls...
    //Therefore we use the symbol tokens instead of html symbols.
    Text(currency match {
        case "EUR" => "€"; //"&euro;"
        case "GBP" => "£"; //"&pound;";
        case "USD" => "$";
        case _ => "&euro;";
    }) ++
    Text(" ") ++
    Text(String.format("%.2f", double2Double(value/100.0)))
  }
}

object Value {
  def apply(property : => Option[Property]) = new Value(property)
  def apply(property : => Property) = new Value2(property)
}

class Value(property : => Option[Property]) extends Function0[NodeSeq]{
  override def apply : NodeSeq = property match { 
    case Some(property) => property.propertyType match {
      case jpa.catalog.PropertyType.Media => 
        if (property.mediaValue == null) 
          <img src={"/images/image-"+property.valueId+".jpg"} />
	      else if (property.mimeType.startsWith("image/")) 
		      <img src={"image/" + property.valueId} />
	       else
  		     Text(property.mediaValue.toString());
      case jpa.catalog.PropertyType.Money =>
        WebshopUtil.formatMoney(property.pvalue.getMoneyCurrency, property.pvalue.getMoneyValue.doubleValue)
      case _ => Text(property.valueAsString)
    }
    case None => Text("")
  }
}
class Value2(property : => Property) extends Value(if (property != null) Some(property) else None){
}

object Link extends BindingHelpers {
  def apply(group : ProductGroup) = (xml : NodeSeq) => 
    new Elem(null, "a", new UnprefixedAttribute("href", "/group/" + group.id, current.attributes), TopScope, xml:_*);
  
  def apply(product : Product) = (xml : NodeSeq) => 
    new Elem(null, "a", new UnprefixedAttribute("href", "/product/" + product.id, current.attributes), TopScope, xml:_*);
  
  def apply(path : String) = (xml : NodeSeq) => <a href={path}>{xml}</a>
}

object LinkAttr {
	def apply(group : ProductGroup) = new LinkAttr(Text("/group/" + group.id))
	def apply(product : Product) = new LinkAttr(Text("/product/" + product.id))
	def apply(path : String) = new LinkAttr(Text(path))
}

class LinkAttr(val value : NodeSeq) {
}
