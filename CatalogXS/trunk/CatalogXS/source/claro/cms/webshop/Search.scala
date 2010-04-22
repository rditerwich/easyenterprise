package claro.cms.webshop

import net.liftweb.http.{S,SHtml}
import xml.{Node, NodeSeq}

class SearchForm extends Bindable {
  var searchString : String = WebshopModel.currentSearchStringVar.is getOrElse("")
  
  override def bindings = Bindings(this, Map(
    "search-string" -> SHtml.text(searchString, searchString = _, 
      ("class", "formfield searchfield"),
      ("onclick", "javascript:this.value=(this.value == 'search' ? '' : this.value);")),
    "submit" -> SHtml.submit("Search", () => S.redirectTo("/search/" + searchString),
      ("class", "formbutton"))))
  
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    <lift:snippet type={"Shopident2"} form="POST">
      { super.bind(node, context) }
    </lift:snippet>
  }
  
  def ident(xml : NodeSeq) : NodeSeq = xml
}