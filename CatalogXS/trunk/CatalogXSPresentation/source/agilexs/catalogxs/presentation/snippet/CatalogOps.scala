package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;

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
  //@EJB private[this] var catalogBean : agilexs.catalogxs.businesslogic.Catalog = _

  object currentCatalog extends
     RequestVar[Box[Catalog]](Empty)

  def list (xhtml : NodeSeq) : NodeSeq = {
	val catalogs = Model.em.createQuery("Select c from Catalog c").getResultList().asInstanceOf[java.util.List[Catalog]]
//    val catalogs = Model.listToWrapper(catalogBean.listCatalogs().asInstanceOf[java.util.List[Catalog]])

    catalogs.flatMap(catalog =>
      bind("catalog", xhtml,
          "link" ->
             SHtml.link("/catalog/" + catalog.getId().toString(),
             () => currentCatalog(Full(catalog)),
             Text(catalog.getName()))))
  }

}
