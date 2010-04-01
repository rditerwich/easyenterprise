package bootstrap.liftweb

import _root_.net.liftweb._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.agilexs.catalogxs.presentation.model._
import _root_.javax.servlet.http.{HttpServletRequest}
import _root_.scala.xml._
import agilexs.catalogxs.presentation.util.ImageDispatcher

//import _root_.java.util.Locale

//import _root_.agilexs.catalogxs._
//import presentation._
//import cache.{definedLocale}

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {

  //val localeCookieName = "cookie.agilexs.catalogxs.locale"

  def boot {
//    Catalog.catalogs.clear()
//    CatalogCache.viewCaches.clear()

//    if (!DB.jndiJdbcConnAvailable_?) 
//      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    //http://wiki.github.com/dpp/liftweb/how-to-localization

/*LIFT 2.0?? 
    LiftRules.resourceBundleFactories.prepend { 
	  case (basename, locale) if localeAvailable_?(locale) => 
	      CacheResourceBundle(locale)
	  case _ => CacheResourceBundle(new Locale("en","GB"))
	}
*/
    // where to search snippet
//    LiftRules.addToPackages("agilexs.catalogxs.presentation");
    
    LiftRules.defaultHeaders = {
      // Permit caching of our home page 
      case (_, Req(List("index"),_,_)) => 
          List("Cache-Control" -> "public, max-age=10800, pre-check=10800", 
                     "Pragma" -> "private") 
      case (_, Req(List("favicon.ico"),_,_)) => 
          List("Cache-Control" -> "public, max-age=10800, pre-check=10800", 
        		  "Pragma" -> "private") 
      case (_, Req("ajax_request" :: rest,_,_)) => 
          List("Cache-Control" -> "public, max-age=10800, pre-check=10800", 
        		  "Pragma" -> "private") 
      case _ => 
          List("Expires" -> "Mon, 26 Jul 1997 05:00:00 GMT", 
                     "Cache-Control" -> 
                     "cache; public; no-store; must-revalidate; max-stale=0; post-check=0; pre-check=0; max-age=0", 
                     "Pragma" -> "public") 
    } 
    
    //LiftRules.localeCalculator = r => definedLocale.openOr(LiftRules.defaultLocaleCalculator(r))

    //We will take care of this
    //Schemifier.schemify(true, Log.infoF _, User)

      
    // Build SiteMap
    //product is added otherwise product rewrite doesn't work
    val entries =
      Menu(Loc("Home", List("index"), "Home")) ::
      Menu(Loc("Group", List("group"), "Group", Hidden)) ::
      Menu(Loc("Product", List("product"), "Product", Hidden)) ::
   	  Menu(Loc("Search", List("search"), "Search", Hidden)) ::
   	  Menu(Loc("ShoppingCart", List("shoppingcart"), "ShoppingCart", Hidden)) ::
// 	    Menu(Loc("Image", List("image"), "Image", Hidden)) ::
      Menu(Loc("Admin", List("admin", "index"), "Admin", Hidden),
           Menu(Loc("productgroups", List("admin", "productgroups"), "Product Groups")),
           Menu(Loc("property", List("admin", "property") -> true, "Property", Hidden)),
           Menu(Loc("products", List("admin", "products"), "Products"))
          ) ::
      Nil
/*
        Menu(Loc("productgroups", List("admin", "productgroups"), "Product Groups")),
        Menu(Loc("property", List("admin", "property"), "Property", Hidden),
             Menu(Loc("property", List("admin", "property", "add"), "Property", Hidden)),
             Menu(Loc("property", List("admin", "property", "new"), "Property", Hidden)),
             Menu(Loc("property", List("admin", "property", "remove"), "Property", Hidden))
          ),
        Menu(Loc("products", List("admin", "products"), "Products"))) ::
*/
    
    
    //User.sitemap
    //LiftRules.setSiteMap(SiteMap(entries:_*))

//    LiftRules.rewrite.append(NamedPF("ShopPathRewriter") {
//      case ShopPathRewriter(response : RewriteResponse) => response 
//    })

//    /**
//    * Parse language url entry
//    */
//    LiftRules.rewrite.append(new LiftRules.RewritePF {
//    	override def apply(request : RewriteRequest) = {
//    		RewriteResponse(request.path.partPath.tail, Map("language" -> request.path.partPath.head))
//    	}
//    	override def isDefinedAt(request : RewriteRequest) = {
//    		!request.path.partPath.isEmpty && 
//    		Model.locales.contains(request.path.partPath.head)
//    		
//    	}
//    })
//    
//    //Rewrite rules to remap urls with id to page with id as argument, e.g. /product/123 -> product with id=123
//    LiftRules.rewrite.append(NamedPF("ProductRewrite") {
//	    case RewriteRequest(
//	    	ParsePath("group" :: group :: Nil, _, _,_), _, _) => 
//	            RewriteResponse("group" :: Nil, Map("currentProductGroup" -> group)
//	    )
//	    case RewriteRequest(
//	    	ParsePath("product" :: product :: Nil, _, _,_), _, _) => 
//	            RewriteResponse("product" :: Nil, Map("currentProduct" -> product)
//	    )
//	    case RewriteRequest(
//	        ParsePath("image" :: imageID :: Nil, _, _,_), _, _) => 
//	            RewriteResponse("image" :: Nil, Map("imageID" -> imageID)
//	    )
//	    case RewriteRequest(
//	        ParsePath("search" :: searchString :: Nil, _, _,_), _, _) => 
//	            RewriteResponse("search" :: Nil, Map("searchString" -> searchString)
//	    )
//    })

    LiftRules.dispatch.prepend(ImageDispatcher.dispatch)
    claro.cms.Cms.boot
    
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

  /**
   * Force the request to be UTF-8
   */
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

// Set up a LoanWrapper to automatically instantiate and tear down the EntityManager on a per-request basis
    S.addAround(List(
      new LoanWrapper { 
		def apply[T] (f : => T): T = {

		  try {
		    f
		  } finally {
//			if (Model.entityManager.isOpen) {
//			  Model.entityManager.close;
//			}
		  }
		}
      }))
  }
}
