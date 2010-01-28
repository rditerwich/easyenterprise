package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;
import javax.naming.InitialContext;

import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
import JsCmds._ 
 
import mapper._ 
 
import util._ 
import Helpers._

import scala.collection.jcl.{BufferWrapper,SetWrapper} 
import scala.xml.{NodeSeq, Text, SpecialNode, Elem} 

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean

class BasicSnippet[A] {
  //this annotation doesn't work....
  @EJB{val name = "ejb/CatalogBean"} private[this] var catalogBean : agilexs.catalogxs.businesslogic.Catalog = _

  /*
  def list(list : BufferWrapper[A], group : String, xhtml : NodeSeq) : NodeSeq = {
    list.flatMap(catalog =>
      bind(group, xhtml,
          "link" ->
             SHtml.link("/catalog/" + catalog.getId().toString(),
             () => currentCatalog(Full(catalog)),
             Text(catalog.getName()))))

    catalogBean = lookupCatalog()
    xhtml
  }

  def show(item : A, group : String, xhtml : NodeSeq) : NodeSeq = {
    val propertyMap = new Array[BindParam](1 + (if (item == null) 0 else item.getPropertyValues().size()))

    propertyMap(0) = "id" -> Text(S.param("product").openOr("fail over product"))
    var i = 1

    if (product != null) {
	    for (pv <- Model.listToWrapper(product.getPropertyValues.asInstanceOf[java.util.List[PropertyValue]])) {
	      propertyMap(i) = pv.getProperty.getName -> getNode(pv)
	      i+=1
	    }
    }
    bind("product", xhtml, propertyMap: _*)
  }
*/

  def lookupCatalog() : agilexs.catalogxs.businesslogic.Catalog = {
    val ic = new InitialContext()
    ic.lookup("java:comp/env/ejb/CatalogBean").asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]
  }

  def getNode(name: String, pv : PropertyValue) : TheBindParam = {
    (name -> 
       (pv.getProperty().getType() match {
         case PropertyType.String =>
        	 Text(pv.getStringValue());
         case PropertyType.Integer =>
        	 Text(pv.getIntegerValue().toString());
         case PropertyType.Boolean =>
        	 Text(pv.getBooleanValue().toString());
         case PropertyType.Money =>
        	 Text(pv.getMoneyValue().toString());
         case PropertyType.Enum =>
           Text(pv.getEnumValue().toString());
         case PropertyType.Media =>
             if (pv.getMimeType().startsWith("image/")) {
               Text("<img src=\"image/"+ pv.getId() + "\" />")
             } else {
        	   Text(pv.getMediaValue().toString());
            }
         case PropertyType.FormattedText =>
             Text(pv.getStringValue());
         case PropertyType.Real =>
           Text(pv.getRealValue().toString());
         case PropertyType.Length =>
           Text(pv.getRealValue().toString());
         case PropertyType.Mass =>
           Text(pv.getRealValue().toString());
         case PropertyType.Time =>
           Text(pv.getRealValue().toString());
         case PropertyType.ElectricCurrent =>
           Text(pv.getRealValue().toString());
         case PropertyType.Temperature =>
           Text(pv.getRealValue().toString());
         case PropertyType.LuminousIntensity =>
           Text(pv.getRealValue().toString());
         case PropertyType.AmountOfSubstance =>        
           Text(pv.getRealValue().toString());
         case PropertyType.Frequency =>
           Text(pv.getRealValue().toString());
         case PropertyType.Angle =>
           Text(pv.getRealValue().toString());
         case PropertyType.Energy =>
           Text(pv.getRealValue().toString());
         case PropertyType.Power =>
           Text(pv.getRealValue().toString());
         case PropertyType.Voltage =>
           Text(pv.getRealValue().toString());
         case PropertyType.Area =>
           Text(pv.getRealValue().toString());
         case PropertyType.Volume =>
           Text(pv.getRealValue().toString());
         case PropertyType.Velocity =>
           Text(pv.getRealValue().toString());
         case PropertyType.Acceleration =>
           Text(pv.getRealValue().toString());
         case _ => Text("")
     })).asInstanceOf[TheBindParam]
  }
}
