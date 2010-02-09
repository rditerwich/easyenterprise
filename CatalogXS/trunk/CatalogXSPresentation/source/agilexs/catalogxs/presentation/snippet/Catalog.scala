package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.http.S

class Catalog extends BasicSnippet[Promotion] {

  def catalog(xhtml: NodeSeq) : NodeSeq = {
	CatalogBindings.catalogBinding(Model.catalog).bind(S.attr("tag") openOr "catalog", xhtml)
  }
  
  def listPromotions(xhtml: NodeSeq) : NodeSeq = {
      val ps = Model.catalog.promotions 
	  Model.catalog.promotions seqFlatMap (CatalogBindings.promotionBinding(_).bind(S.attr("tag") openOr "promotion", xhtml))
  }
}  
