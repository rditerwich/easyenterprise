package claro.cms.components

import net.liftweb.http.S
import claro.cms.Request
import claro.cms.Component

class MenuComponent extends Component {

  val prefix = "menu"
  
  bindings.append {
    case _ : MenuComponent => Map(
      "list" -> 12 -> "menu-item",
      "item" -> new MenuItem(@@("path","qq")) -> "menu-item")
    case item : MenuItem => Map(
      "@class" -> item.getClass("W") -> "class",
      "href" -> item.link -> "href"
    )
  }
}

case class MenuItem(link : String) {
  
  def getClass(s : String) : String = {
    val elts = s.split(';')
    if (S.uri.startsWith(link)) 
      if (elts.size > 0) elts(0)
      else ""
    else 
      if (elts.size > 1) elts(1)
      else ""
  }
}
