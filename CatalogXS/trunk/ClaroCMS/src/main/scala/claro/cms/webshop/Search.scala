package claro.cms.webshop

import xml.{Node, NodeSeq}
import net.liftweb.http.{S,SHtml}
import claro.jpa
import claro.cms.{Bindable,Bindings,BindingContext}

class SearchForm extends Bindable {
	override val defaultPrefix = "search"
  val dummySearch = "Search"
  var searchString : String = WebshopModel.currentSearchStringVar.is getOrElse(dummySearch)
  
  val pathPrefix = WebshopModel.currentCategory match {
    case Some(category) => "/category/" + category.urlName
    case None => ""
  }
  
  override lazy val bindings = Map(
    "search-string" -> SHtml.text(searchString, s => searchString = if (s == dummySearch) dummySearch else s, 
      ("onclick", "javascript:if (value == '" + dummySearch + "') value = '';"),
      ("onblur", "javascript:if (value == '') value = '" + dummySearch + "';")) % currentAttributes(),
    "submit" -> SHtml.submit("Search", () => if (searchString != dummySearch) S.redirectTo(pathPrefix + "/search/" + searchString)) % currentAttributes())
  
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    <form method="post" action={S.uri}>
      { super.bind(node, context) }
    </form>
  }
}