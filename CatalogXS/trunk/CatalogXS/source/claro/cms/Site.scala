package claro.cms

import xml.{Elem,NodeSeq}
import java.util.Properties
import java.io.{File,FileReader}
import java.net.URI
import javax.persistence.Persistence
import claro.common.util.SubDirs
import claro.common.util.Conversions._
import net.liftweb.util.Log

object Site {
  val defaultSite = new Site(new File(System.getProperty("user.home") + "/sites/default/site.config").toURI)
  val sites : List[Site] = findSites(System.getProperty("sites") getOrElse (""))
  val sitesByServer : Map[String,Seq[Site]] = sites groupBy (_.server)
  val sitesByPath : Map[String,Seq[Site]] = sites groupBy (_.contextPath)
  
  def findSites(path : String) : List[Site] = {
    val uris : List[URI] = path split(",") filter(!_.trim.isEmpty) map(new URI(_).canonical) toList 
    val siteFiles : List[URI] = uris flatMap (_.find(4, _.name == "site.config"))
    val siteFiles2 : List[URI] = uris map (_.resolve("site.config")) filter(!siteFiles.contains(_)) filter(_.exists)
    (siteFiles ++ siteFiles2) map (uri => new Site(uri)) match { 
      case Nil => defaultSite :: Nil
      case list => list
    }
  }

  def findSite(server : String, contextPath : String) : Site = {
    val sites = sitesByServer get(server) match {
      case Some(sites) => sites
      case None => sitesByServer get("") match {
        case Some(sites) => sites
        case None => Seq()
      }
    }
    sites find (site => contextPath.startsWith (site.contextPath)) match {
      case Some(site) => site
      case None => defaultSite
    }
  }
}

class Site(val siteFile : URI) {
  val location = siteFile.resolve(".")
  val config = new SiteConfig(siteFile)
  val properties = new Properties(System.getProperties).load(siteFile)
  val name = config.name
  val server = config.server
  val contextPath = config.path.mkString("/", "/", "")
  val templateStore = new UriStore(Seq(location))
  val templateCache = new TemplateCache(this)
  val caching = false
  
  // components
  val components : Seq[Component] = {
    val comps = Cms.components.toList map (_())
    val compNames = Set(comps.map(_.getClass.getName):_*)
    val comps2 = config.components filter(!compNames.contains(_)) map {  
      try {
        getClass.getClassLoader.loadClass(_).newInstance.asInstanceOf[Component]
      } catch {
        case _ : Throwable => null
      }
    } filter (_ != null)
    comps ++ comps2
  } 
  
  val templateLocators = components flatMap (_.templateLocators.toList)
  val bindings = components flatMap (_.bindings.toList)

  val rootBinding = new RootBinding(this)
  
  lazy val emProperties = properties.parseAll("entitymanager.")
  
  def entityManagerFactory(name : String) =  
	  Persistence.createEntityManagerFactory(name, emProperties toJava)
  
  override def toString = {
    "Site location " + location.toString
  }
  
  Log.info("Found site: " + this)
}


class SiteConfig(val siteFile : URI) {
  val properties = new Properties(System.getProperties).load(siteFile)
  val name = properties("site.name")
  val server = properties("site.server")
  val path : List[String] = properties("site.path") getOrElse ("/") split("/") filter(!_.isEmpty) toList 
  val components : List[String] = properties("site.components") split(",") filter(!_.trim.isEmpty) toList
}