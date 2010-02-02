package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.presentation.model.Bindings
import agilexs.catalogxs.presentation.model.Model._
import agilexs.catalogxs.jpa.catalog._

class Promotions extends BasicSnippet[Promotion] {

  lazy val catalogBean = lookupCatalog()

  def all(xhtml: NodeSeq) : NodeSeq = {
      val promotions = catalogBean.findAllPromotions(0, 9) 
      promotions flatMap (Bindings.promotionBindings(_)(xhtml))
  }
}  
