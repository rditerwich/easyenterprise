package claro.cms

import xml.{Node, NodeSeq}
import net.liftweb.http.{RequestVar,S}

object Paging extends RequestVar[Paging](new Paging)

class Paging extends Bindable {
	var currentPage = 1
	var sizeEstimate = 0
	var pageSize = 1
	var pageCount = 5

	lazy val calcuated = {
		var startPage = currentPage - 1 - pageCount / 2
		val maxPage = Math.max(Math.ceil(sizeEstimate / pageSize).toInt, (currentPage - 1))
		startPage = Math.min(startPage, maxPage - pageCount)
		startPage = Math.max(0, startPage)
		val endPage = Math.min(startPage + pageCount, maxPage)
		Calcuated(startPage != 0, endPage != maxPage, currentPage > 1, sizeEstimate > currentPage * pageSize, (startPage + 1).to(endPage + 1))
	}
	
	override val prefix = "paging"
	
	override def bind(node : Node, context : BindingContext) = {
		pageCount = attr(node, "page-count", _.toInt, 5)
		val xml = Binding.bind(node.child, context)
		Binding.bind(xml, childContext(node, context))
	}
	
	override def bindings = Map(
	  "single-page" -> (calcuated.pages.size <= 1),
	  "multi-page" -> (calcuated.pages.size > 1),
	  "previous" -> ((xml : NodeSeq) => 
		  if (calcuated.previous) <a class="paging-previous active" href={href(currentPage - 1)}>{xml}</a>
		  else <a class="paging-previous inactive">{xml}</a>),
	  "next" -> ((xml : NodeSeq) => 
			if (calcuated.next) <a class="paging-next active" href={href(currentPage + 1)}>{xml}</a>
			else <a class="paging-next inactive">{xml}</a>),
	  "page" -> ((xml : NodeSeq) => calcuated.pages.map(page => 
	    if (page == currentPage) <a class="paging-nr current">{page}</a>
	    else <a class="paging-nr" href={href(page)}>{page}</a>)))
	
	 def href(page : Int) = {
	  val uri = if (currentPage > 0) {
	 	  val uri = S.uri
		  val i2 = S.uri.lastIndexOf('/')
		  val i = S.uri.lastIndexOf('/', i2 - 1)
		  if (uri.substring(i).startsWith("/page/")) uri.substring(0, i)
		  else uri
	  } else S.uri 
	  if (page > 1) uri + "/page/" + page
	  else uri
  }
  
	case class Calcuated(less : Boolean, more : Boolean, previous : Boolean, next : Boolean, pages : Seq[Int])
}
