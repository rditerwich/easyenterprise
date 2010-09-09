package claro.cms

import net.liftweb.http.{LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.common.util.Locales

object Rewrite extends LiftRules.RewritePF {

  def isDefinedAt(request : RewriteRequest) = true
  
  def apply(request : RewriteRequest) : RewriteResponse = {
	val parsePath = request.path
	var partPath = parsePath.partPath
	if (!partPath.isEmpty && Website.instance.config.locales.contains(partPath.head)) {
		Cms.locale.set(Locales(parsePath.partPath.head))
		partPath = partPath.tail
	}
	partPath = parsePage(Nil, partPath) match {
	  case Some((partPath, page)) => 
		Paging.currentPage = page 
		partPath
	  case None => partPath
	}
	RewriteResponse(ParsePath(partPath, parsePath.suffix, parsePath.absolute, parsePath.endSlash), Map.empty, true)
  }
  
  def parsePage(partPath : List[String], remaining : List[String]) : Option[(List[String], Int)] = remaining match {
	  case "page" :: page :: Nil => Some((partPath, page.toInt))
	  case head :: tail => parsePage(partPath ::: List(head), tail)
	  case _ => None
  }
}