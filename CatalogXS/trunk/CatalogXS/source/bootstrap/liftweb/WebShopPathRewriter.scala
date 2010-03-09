package bootstrap.liftweb

import agilexs.catalogxs.presentation.model.Model
import agilexs.catalogxs.presentation.model.Shop

import net.liftweb.http.{RewriteRequest,RewriteResponse}

/**
 * Parse an http request, extract the webshop id, language and object to show.
 */
object ShopPathRewriter {

  def unapply(request : RewriteRequest) : Option[RewriteResponse] = {
    
    // make resilient to trailing slash
    val path = request.path.partPath.dropRight(if (request.path.endSlash) 1 else 0)
    
    // extract webshop from url
    getShop(request.httpRequest.getServerName, path) match {
      case None => None
      case Some((shop, basePath0, path0)) =>
        
        // extract language from url
        val (language,basePath,path) = getLanguage(shop, basePath0, path0)
        
        // these are stored in S, to be read by Model class later
        val map = Map("shop" -> shop.id.toString, "language" -> language, "basePath" -> basePath.mkString("/", "/", ""))
        
        path match {
          case "product" :: product :: Nil => Some(RewriteResponse("product" :: Nil, map + ("product" -> product)))
          case "group" :: group :: Nil => Some(RewriteResponse("group" :: Nil, map + ("group" -> group)))
          case "image" :: image :: Nil => Some(RewriteResponse(Nil, map + ("image" -> image)))
          case "search" :: search :: Nil => Some(RewriteResponse(Nil, map + ("search" -> search)))
          
          // handles default url without trailing slash 
          case Nil => Some(RewriteResponse("index" :: Nil, map))
          
          case _ => None
        }
    }
  }
  
  def getShop(serverName : String, path : List[String]) : Option[(Shop,List[String],List[String])] = {
    val cache = Model.shopCache.get
    if (serverName == "localhost" || serverName == "127.0.0.1") {
    	if (path.isEmpty) None
        else cache.shopsByName.get(path.head) match {
          case Some(shop) => Some((shop, path.head :: Nil, path.tail))
          case None => None
        }
    } else {
      cache.shopsByServerName(serverName).find(s => path.startsWith(s.prefixPath)) match {
    	case Some(shop) => Some((shop, path.take(shop.prefixPath.length), path.drop(shop.prefixPath.length)))
    	case None => None
      }
    }
  }

        
  def getLanguage(shop : Shop, basePath : List[String], path : List[String]) : (String,List[String],List[String]) = {
    path match {
      case List() => (shop.defaultLanguage, basePath, path)
      case head :: tail => 
        if (Model.locales.contains(head)) (head, basePath ::: List(head), tail)
        else (shop.defaultLanguage, basePath, path)
    }
  }
}
