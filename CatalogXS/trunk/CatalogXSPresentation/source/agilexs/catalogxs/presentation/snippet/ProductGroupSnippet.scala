package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;
import javax.naming.InitialContext;
import java.util.HashMap
import java.util.ArrayList
import java.util.List

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

import agilexs.catalogxs.presentation.cache._
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.businesslogic._
import agilexs.catalogxs.jpa.catalog._


class ProductGroupSnippet extends BasicSnippet[ProductGroup] {

  object currentProductGroup extends RequestVar[Box[ProductGroup]](Empty)
  object currentProduct extends RequestVar[Box[Product]](Empty)
  object cpg extends SessionVar[ProductGroup](null)
  object ctaxonomy extends SessionVar[Taxonomy](null)
  
  def navList(xhtml : NodeSeq) : NodeSeq = {
    updateCurrentProductGroup()
    val catalogBean = lookupCatalog()
    val pgs = Model.listToWrapper(catalogBean.findAllProductGroupChildren(ctaxonomy.is, cpg.is).asInstanceOf[java.util.List[ProductGroup]])

    pgs.flatMap(productGroup =>
      bind("pg", xhtml,
          "link" ->
             SHtml.link("/productgroup/" + productGroup.getId().toString(),
             () => currentProductGroup(Full(productGroup)),
             Text(LabelCache.getLabel(productGroup.getLabels(), productGroup.getName())))))
  }

  def listProducts(xhtml : NodeSeq) : NodeSeq = {
    updateCurrentProductGroup()
    val catalogBean = lookupCatalog()
    val products = Model.listToWrapper(catalogBean.findAllByProductGroupProducts(0, 20, cpg.is).asInstanceOf[java.util.List[Product]])
    val pl : List[HashMap[String, PropertyValue]] = new ArrayList[HashMap[String, PropertyValue]]();

    for (product <- products) {
      val pvMap : HashMap[String, PropertyValue] = new HashMap[String, PropertyValue]();
      for (pv <- Model.listToWrapper(product.getPropertyValues.asInstanceOf[java.util.List[PropertyValue]])) {
    	  pv.setProduct(product)
	      pvMap.put(pv.getProperty.getName(), pv)
      }
      pl.add(pvMap)
    }
    Model.listToWrapper(pl).flatMap(pMap => 
      bind("p", xhtml,
    	  "ArticleNumber" -> propToLink(pMap),
          getNode("ProductDescription", pMap.get("ProductDescription")),
          getNode("ProductPriceNew", pMap.get("ProductPriceNew")) 
             ))
  }

  private def propToLink(pMap: HashMap[String, PropertyValue]) : NodeSeq = {
    if(pMap.get("ArticleNumber") != null)
      SHtml.link("/product/" + pMap.get("ArticleNumber").getProduct().getId().toString(),
          () => currentProduct(Full(pMap.get("ArticleNumber").getProduct())),
    		  getNode("ArticleNumber", pMap.get("ArticleNumber")).value)
    else Text("ArticleNumber")
  }
  
  def updateCurrentProductGroup() = {
    updateCurrentTaxenomy();
//    cpg.set(
//      S.param("pgID").openOr(null) match {
//	      case null => null
//	      case x => {
//	        val catalogBean = lookupCatalog();
//	        catalogBean.findProductGroupById(x.toLong)
//	        }
//	    })
    val pgId = S.param("pgID").openOr(null);
    if (pgId != null) {
      val catalogBean = lookupCatalog();
      cpg.set(catalogBean.findProductGroupById(pgId.toLong));
    } else {
      cpg.set(null)
    }
  }

  def updateCurrentTaxenomy() = {
	  //if (ctaxonomy.is != null) {
		  val catalogBean = lookupCatalog();
		  ctaxonomy.set(catalogBean.findTaxonomyById(1L));
	  //}
  }
}
