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

import scala.xml.{NodeSeq, Text, SpecialNode} 

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.businesslogic._
import agilexs.catalogxs.jpa.catalog._

class ProductOps {

  /**
   * This method query's the product passed via the get argument. e.g. /product/<id>
   * and maps all property values queried to lift properties based on the property name.
   * For example if there is a property description than it's bind to <product:description/>.
   * This tag can than be used in the template and will display the property value.
   * 
   * TODO: now only Text types are supported, but links and images are also required.
   *       preferable this needs to be in the database, so it can automatically be
   *       be calculated based to generate the specific binding, i.e. Text or SHtml.link.
   */
  def show(xhtml : NodeSeq) : NodeSeq = {
    val catalogBean = lookupCatalog   
    val product = catalogBean.findProductById((S.param("product").openOr("0")).toLong)
    val propertyMap = new Array[BindParam](1 + (if (product == null) 0 else product.getPropertyValues.size))

    propertyMap(0) = "id" -> Text(S.param("product").openOr("fail over product"))
    var i = 1

    if (product != null) {
	    for (pv <- Model.listToWrapper(product.getPropertyValues.asInstanceOf[java.util.List[PropertyValue]])) {
	      propertyMap(i) = pv.getProperty.getName -> Text(pv.getStringValue)
	      i+=1
	    }
    }
    bind("product", xhtml, propertyMap: _*)
  }

    private def lookupCatalog : agilexs.catalogxs.businesslogic.Catalog = {
      val ic = new InitialContext
      return ic.lookup("java:comp/env/ejb/CatalogBean").asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]    
  }

}
