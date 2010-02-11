package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.util.Full
import net.liftweb.http.S

class Catalog extends BasicSnippet[Promotion] {

  def catalog(xml : NodeSeq) : NodeSeq = {
	CatalogBindings.catalogBinding(Model.catalog).bind(S.attr("tag") openOr "catalog", xml) 
  }
}  
