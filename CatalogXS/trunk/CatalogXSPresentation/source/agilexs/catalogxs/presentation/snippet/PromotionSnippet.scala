package agilexs.catalogxs.presentation.snippet

import javax.ejb.EJB;
import javax.naming.InitialContext;
import java.util.HashMap
import java.util.ArrayList
import java.util.List

import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
import JsCmds._ 
 
import mapper._ 
 
import util._ 
import Helpers._

import scala.xml.{NodeSeq, Text, SpecialNode} 

import agilexs.catalogxs.presentation.cache._
import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Conversions._
import agilexs.catalogxs.businesslogic._
import agilexs.catalogxs.jpa.catalog._

class PromotionSnippet extends BasicSnippet[Promotion] {

    def list(xhtml : NodeSeq) : NodeSeq = {
      def getBinds(promotion : Promotion) : Array[BindParam] = promotion match {
        case v : VolumeDiscountPromotion =>
          val product = v.getProduct()
          val propertyMap = new Array[BindParam](6 + (if (product == null) 0 else product.getPropertyValues().size()))
  
          propertyMap(0) = "id" -> Text(S.param("product").openOr("fail over product"))
          propertyMap(1) = "PromotionStartDate" -> Text(v.getStartDate.formatted("YYYY/mm/dd"))
          propertyMap(2) = "PromotionEndDate" -> Text(v.getStartDate.formatted("YYYY/mm/dd"))
          propertyMap(3) = "PromotionPrice" -> Text(v.getPrice.toString)
          propertyMap(4) = "PromotionCurrency" -> Text(v.getPriceCurrency.toString)
          propertyMap(5) = "PromotionVolumeDiscount" -> Text(v.getVolumeDiscount.toString)
          var i = 6

          if (product != null) {
              for (pv <- product.getPropertyValues.asInstanceOf[java.util.List[PropertyValue]]) {
                propertyMap(i) = getNode(pv.getProperty.getName, pv)
                i+=1
              }
          }
          return propertyMap;
        case _ : Promotion => Array[BindParam]()
      }
      val catalogBean = lookupCatalog()
      val promotions = catalogBean.findAllPromotions().asInstanceOf[java.util.List[Promotion]]

      promotions.flatMap(promotion => bind("p", xhtml, getBinds(promotion): _*))
  }
}
