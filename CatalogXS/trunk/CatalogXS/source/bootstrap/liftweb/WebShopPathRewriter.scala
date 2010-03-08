package bootstrap.liftweb

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.WebShop

import net.liftweb.http.{RewriteRequest,RewriteResponse}

/**
 * Parse an http request, extract the webshop id, language and object to show.
 */
object WebShopPathRewriter {

  def unapply(request : RewriteRequest) : Option[RewriteResponse] = {
    println("Web shop selector for request: " + request)
  
    getWebShop(request.httpRequest.getServerName, request.path.partPath) match {
      case None => None
      case Some((webShop, path)) =>
        getLanguage(webShop, path) match {
          case None => None
          case Some((language, path)) =>
            path match {
              case "product" :: product :: Nil => Some(RewriteResponse(Nil, Map("product" -> product, "language" -> language, "webShop" -> webShop.id.toString)))
              case "group" :: group :: Nil => Some(RewriteResponse(Nil, Map("group" -> group, "language" -> language, "webShop" -> webShop.id.toString)))
              case "image" :: image :: Nil => Some(RewriteResponse(Nil, Map("image" -> image, "language" -> language, "webShop" -> webShop.id.toString)))
              case "search" :: search :: Nil => Some(RewriteResponse(Nil, Map("search" -> search, "language" -> language, "webShop" -> webShop.id.toString)))
              case _ => Some(RewriteResponse(path, Map("language" -> language, "webShop" -> webShop.id.toString)))
	        }
        }
    }
  }
  
  def getWebShop(serverName : String, path : List[String]) : Option[(WebShop, List[String])] = {
    val cache = Model.webShopCache.get
    if (serverName == "localhost" || serverName == "127.0.0.1") {
    	if (path.isEmpty) None
        else cache.webShopsByName.get(path.head) match {
          case Some(webShop) => Some((webShop, path.tail))
          case None => None
        }
    } else {
      cache.webShopsByServerName(serverName).find(s => path.startsWith(s.prefixPath)) match {
    	case Some(webShop) => Some((webShop, path.drop(webShop.prefixPath.length)))
    	case None => None
      }
    }
  }

        
  def getLanguage(webShop : WebShop, path : List[String]) : Option[(String,List[String])] = {
    path match {
      case List() => Some((webShop.defaultLanguage, path))
      case head :: tail => 
        if (Model.locales.contains(head)) Some((head, tail))
        else Some((webShop.defaultLanguage, path))
    }
  }
}
