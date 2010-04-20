package agilexs.catalogxs.presentation.snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq 
import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._
import net.liftweb.util.Full
import net.liftweb.http.S
import net.liftweb.http.SHtml

class Shop {

  def shop(xml : NodeSeq) : NodeSeq = {
	ShopBindings.shopBinding(Model.shop).bind(S.attr("tag") openOr "shop", xml) 
  }
  
  def ident(xml : NodeSeq) : NodeSeq = xml
  
  def search(xml : NodeSeq) : NodeSeq = {
    var searchString = Model.currentSearchString getOrElse("")

    bind("search", xml, 
      "searchString" -> SHtml.text(searchString, searchString = _, 
        "class" -> "formfield searchfield",
        "onclick" -> "javascript:this.value=(this.value == 'search' ? '' : this.value);"),
      "submit" -> SHtml.submit("Search", () => S.redirectTo(Model.basePath + "/search/" + searchString),
        "class" ->  "formbutton"))
  }
}  
