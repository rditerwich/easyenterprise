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

import scala.xml.{NodeSeq, Text} 

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean

/**
 * Catalog Operations 
 */
class CatalogOps {
  //this annotation doesn't work....
  @EJB{val name = "ejb/CatalogBean"} private[this] var catalogBean : agilexs.catalogxs.businesslogic.Catalog = _

  object currentCatalog extends RequestVar[Box[Catalog]](Empty)
  object currentProduct extends RequestVar[Box[Product]](Empty)

  def list (xhtml : NodeSeq) : NodeSeq = {
    catalogBean = lookupCatalog()
    val catalogs = Model.listToWrapper(catalogBean.listCatalogs().asInstanceOf[java.util.List[Catalog]])

    catalogs.flatMap(catalog =>
      bind("catalog", xhtml,
          "link" ->
             SHtml.link("/catalog/" + catalog.getId().toString(),
             () => currentCatalog(Full(catalog)),
             Text(catalog.getName()))))
  }

  def listProducts(xhtml : NodeSeq) : NodeSeq = {
    catalogBean = lookupCatalog()
    val cid = catalogBean.findCatalogById(S.param("catalog").openOr("0").toLong)
    val products = Model.listToWrapper(catalogBean.findProductsByCatalogId(cid).asInstanceOf[java.util.List[Product]])

    products.flatMap(product =>
      bind("product", xhtml,
          "link" ->
             SHtml.link("/product/" + product.getId().toString(),
             () => currentProduct(Full(product)),
             Text(product.getId().toString()))))
  }

  private def lookupCatalog() : agilexs.catalogxs.businesslogic.Catalog = {
    val ic = new InitialContext()
    ic.lookup("java:comp/env/ejb/CatalogBean").asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]
  }
}
