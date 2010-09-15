package agilexs.catalogxs.presentation.cache

import javax.ejb.EJB;
import javax.naming.InitialContext;

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean

class BasicCache[A] {

    //this annotation doesn't work....

  def lookupCatalog() : agilexs.catalogxs.businesslogic.Catalog = {
    val ic = new InitialContext()
    ic.lookup("java:comp/env/ejb/CatalogBean").asInstanceOf[agilexs.catalogxs.businesslogic.Catalog]
  }
}
