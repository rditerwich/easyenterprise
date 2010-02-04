package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model.{CatalogCache, DefaultTag}
import agilexs.catalogxs.presentation.model.CatalogBindings._
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.presentation.model.Tag


import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.http.S

class Catalog extends BasicSnippet[Promotion] {

  lazy val catalogBean = lookupCatalog()

  def listPromotions(xhtml: NodeSeq) : NodeSeq = {
    
	  // TODO store in global or session space somewhere
	  val catalog = CatalogCache("staples", "webshop", "en")
   
      catalog.promotions flatMap (promotionBindings(_, Tag(S.attr("tag") openOr "promotion"), xhtml))
  }
}  
