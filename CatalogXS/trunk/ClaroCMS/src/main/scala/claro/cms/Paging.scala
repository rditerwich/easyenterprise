package claro.cms

import xml.{Node, NodeSeq}
import net.liftweb.http.{RequestVar,S}

object Paging extends RequestVar[Paging](new Paging)

class Paging {
	var currentPage = 1
	var sizeEstimate = 0
	var pageSize = 1
	def surroundingPages(max : Int) : (Boolean, Seq[Int], Boolean) = {
		var startPage = currentPage - 1 - max / 2
		val maxPage = Math.max(Math.ceil(sizeEstimate / pageSize).toInt, (currentPage - 1))
		startPage = Math.min(startPage, maxPage - max)
		startPage = Math.max(0, startPage)
		val endPage = Math.min(startPage + max, maxPage)
		
		(startPage != 0, (startPage + 1).to(endPage + 1), endPage != maxPage)
	}

	def hasPrevious = currentPage > 1
	def hasNext = sizeEstimate > currentPage * pageSize
}

object PagingBindable extends Binding {
  override def bind(node : Node, context : BindingContext) = {
      val xml = Binding.bind(node.child, context)
      val paging = Paging.is
      val (less, pages, more) = paging.surroundingPages(5)
      val bindings : Bindings = Bindings(paging, 
          "single-page" -> new AnyBinding(pages.size <= 1),
          "multi-page" -> new AnyBinding(pages.size > 1),
          "previous" -> new XmlBinding(xml => 
	        if (paging.hasPrevious) <a class="paging-previous active" href={href(paging.currentPage - 1)}>{xml}</a>
	        else <a class="paging-previous inactive">{xml}</a>),
          "page" -> new XmlBinding(xml => pages.map { page => 
            if (page == paging.currentPage) <a class="paging-nr current">{page}</a>
            else <a class="paging-nr" href={href(page)}>{page}</a>}),
          "next" -> new XmlBinding(xml => 
          	if (paging.hasNext) <a class="paging-next active" href={href(paging.currentPage + 1)}>{xml}</a>
          	else <a class="paging-next inactive">{xml}</a>))
      Binding.bind(xml, context + ("paging", bindings))
  }
  
  def href(page : Int) = {
	  val uri = if (Paging.currentPage > 0) {
	 	  val uri = S.uri
		  val i2 = S.uri.lastIndexOf('/')
		  val i = S.uri.lastIndexOf('/', i2 - 1)
		  if (uri.substring(i).startsWith("/page/")) uri.substring(0, i)
		  else uri
	  } else S.uri 
	  if (page > 1) uri + "/page/" + page
	  else uri
  }
}
