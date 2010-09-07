package claro.cms

import xml.{Node, NodeSeq}
import net.liftweb.http.{RequestVar}

object Paging extends RequestVar[Paging](new Paging)

class Paging {
	var currentPage = 0
	var sizeEstimate = 0
	var pageSize = 1
	def surroundingPages(max : Int) : (Boolean, Seq[Int], Boolean) = {
		var startPage = currentPage - max / 2
		val maxPage = Math.max(Math.ceil(sizeEstimate / pageSize).toInt - 1, currentPage)
		startPage = Math.min(startPage, maxPage - max)
		startPage = Math.max(0, startPage)
		val endPage = Math.min(startPage + max, maxPage)
		
		(startPage != 0, startPage.to(endPage), endPage != maxPage)
	}

	def hasPrevious = currentPage > 0
	def hasNext = sizeEstimate > (currentPage + 1) * pageSize
}

object PagingBindable extends Bindable {
  override def bind(node : Node, context : BindingContext) = {
      val xml = Binding.bind(node.child, context)
      val paging = Paging.is
      val (less, pages, more) = paging.surroundingPages(5)
      val bindings : Bindings = Bindings(paging, 
          "previous" -> new XmlBinding(xml => <a class={if (paging.hasPrevious) "paging-previous active" else "paging-previous inactive"}>{xml}</a>),
          "page" -> new XmlBinding(xml => pages.map { page => 
            <a class={if (page == paging.currentPage) "paging-nr current" else "paging-nr"}>{page}</a>
          }),
          "next" -> new XmlBinding(xml => <a class={if (paging.hasNext) "paging-next active" else "paging-next inactive"}>{xml}</a>))
      Binding.bind(xml, context + ("paging", bindings))
//  	 	  "next" -> new XmlBinding(xml => <a class={if (paging.hasNext) "active" else "inactive"}>{xml.child}</a>)
  }
}
