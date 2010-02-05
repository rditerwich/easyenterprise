package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.CatalogCache
import agilexs.catalogxs.presentation.model.CatalogBindings._
import agilexs.catalogxs.presentation.model.Conversions._

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.http.S

class Catalog extends BasicSnippet[Promotion] {

  def listPromotions(xhtml: NodeSeq) : NodeSeq = {
	  Model.catalogCache.promotions flatMap ((promotion) => promotionBinding(promotion).bind(S.attr("tag") openOr "promotion", xhtml))
  }
  
}  
