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

class AdminProduct extends BasicSnippet[Product] {
  def list(xhtml : NodeSeq) : NodeSeq = {
    val catalogBean = lookupCatalog()
    xhtml
  }

  def show(xhtml : NodeSeq) : NodeSeq = {
    val catalogBean = lookupCatalog()
    xhtml
  }
}
