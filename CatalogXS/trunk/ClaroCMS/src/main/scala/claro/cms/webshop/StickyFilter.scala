package claro.cms.webshop

import net.liftweb.http.SessionVar
import claro.cms.Bindable

object StickyFilters extends SessionVar[StickyFilters](new StickyFilters)

class StickyFilters {
	
}

case class StickyFilter(title : String, filter: Product => Boolean){
	
}
