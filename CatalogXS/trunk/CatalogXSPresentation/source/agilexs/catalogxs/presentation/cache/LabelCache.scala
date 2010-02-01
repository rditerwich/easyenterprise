package agilexs.catalogxs.presentation.cache

import java.lang.Long
import java.util.HashMap
import java.util.Collection
import java.util.Locale

import scala.collection.mutable

import net.liftweb._ 
import util._
import Helpers._
import http._
import http.S
import mapper._
import S._
import util._

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Model.{setToWrapper,listToWrapper}
import agilexs.catalogxs.jpa.catalog._
import agilexs.catalogxs.businesslogic.CatalogBean

//Fix: the use of the local, do something with LiftRules.localeCalculator in Boot
object definedLocale extends SessionVar[Box[Locale]](Empty)

object LabelCache extends BasicCache[Label] {

  var labels = new HashMap[String, HashMap[Long, Label]]()
  var labels2 = new mutable.HashMap[Long, Label]
  
  var currentLocal = "en"

  def getLabel(lang : String, id : Long) : String =  {
    init
    labels.get(lang).get(id).getLabel();
  }

  def getLabel(lbls : Collection[Label], default:String) : String =  {
     init
     Model.listToWrapper(lbls.asInstanceOf[java.util.List[Label]]).
       find(x => x.getLanguage().equals(currentLocal)) match {
         case Some(y) => y.getLabel()
         case _ => default
       }
  }
  
  def refresh = {
    labels = null
    init
  }

  private def init = {
    if (labels == null) {
       val catalogBean = lookupCatalog()
       val dbLabels = catalogBean.findAllNonPagedLabels();

       for(lbl <- dbLabels) {
         val lng = lbl.getLanguage()
         var langhm = labels.get(lng);
         
         if (labels.get(lng) == null) {
        	 langhm = new HashMap[Long, Label]()
             labels.put(lbl.getLanguage(), langhm)
         }
         langhm.put(lbl.getId(), lbl)
       }
    }
  }
}
