package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;

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
import agilexs.catalogxs.businesslogic._
import agilexs.catalogxs.jpa.catalog._

class ProductOps {
/*
  @EJB var catalogBean : CatalogBean = null

  def show(xhtml : NodeSeq) = {
    def getProduct(productId : Long) : Product = {
      val query = Model.em.createQuery("Select p from Product p where p.id = :id")
      query.setParameter("id", productId)

      val queryProps = Model.em.createQuery("Select pv from PropertyValues pv where pv.product = :product")

      val product = query.getResultList().get(0).asInstanceOf[Product]
      return product
    } 
      
    var product = getProduct(1L)

    def doBind(xhtml : NodeSeq) =
      bind("product", xhtml,
           "" -> Text(
             product.getPropertyValues.asInstanceOf[
               java.util.ArrayList[PropertyValue]].get(0).getStringValue)
           )
    doBind(xhtml)
  }
*/  
}
