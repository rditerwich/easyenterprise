package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.CatalogModel
import agilexs.catalogxs.presentation.model.CatalogBindings._
import agilexs.catalogxs.presentation.model.Conversions._

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.http.S

class Catalog extends BasicSnippet[Promotion] {

  def listPromotions(xhtml: NodeSeq) : NodeSeq = {
	  CatalogModel.promotions seqFlatMap (promotionBinding(_).bind(S.attr("tag") openOr "promotion", xhtml))
  }
}  
