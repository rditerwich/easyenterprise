package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.presentation.model.{CatalogBindings, CatalogCache}
import agilexs.catalogxs.presentation.model.Conversions._

class Promotions extends BasicSnippet[Promotion] {

  lazy val catalogBean = lookupCatalog()

  def all(xhtml: NodeSeq) : NodeSeq = {
    
	  // TODO store in global or session space somewhere
	  val catalog = CatalogCache("staples", "webshop", "en")
    
      catalog.promotions flatMap (CatalogBindings.promotionBindings(_)(xhtml))
  }
}  
