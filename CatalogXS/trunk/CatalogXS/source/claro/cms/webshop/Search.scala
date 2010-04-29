package claro.cms.webshop

import net.liftweb.http.{S,SHtml}
import xml.{Node, NodeSeq}

class SearchForm extends Bindable {
  val dummySearch = "Search"
  var searchString : String = WebshopModel.currentSearchStringVar.is getOrElse(dummySearch)
  
  val pathPrefix = WebshopModel.currentProductGroup match {
    case Some(group) => "/group/" + group.id
    case None => ""
  }
  
  override def bindings = Bindings(this, Map(
    "search-string" -> SHtml.text(searchString, s => searchString = if (s == dummySearch) dummySearch else s, 
      ("onclick", "javascript:if (value == '" + dummySearch + "') value = '';"),
      ("onblur", "javascript:if (value == '') value = '" + dummySearch + "';")) % currentAttributes(),
    "submit" -> SHtml.submit("Search", () => if (searchString != dummySearch) S.redirectTo(pathPrefix + "/search/" + searchString)) % currentAttributes()))
  
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    <lift:snippet type={"Shop:ident"} form="POST">
      { super.bind(node, context) }
    </lift:snippet>
  }
}