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
  val unsortedSites : List[Site] = findSites(System.getProperty("sites") getOrElse (""))
  val sites : List[Site] = unsortedSites.sort((s1,s2) => s1.config.path.length > s2.config.path.length)
  val sitesByServer : Map[String,Seq[Site]] = sites groupBy (_.server)
  
  def findSites(path : String) : List[Site] = {
    val uris : List[URI] = path split(",") filter(!_.trim.isEmpty) map(new URI(_).canonical) toList 
    val siteFiles : List[URI] = uris flatMap (_.find(4, _.name == "site.config"))
    val siteFiles2 : List[URI] = uris map (_.child("site.config")) filter(!siteFiles.contains(_)) filter(_.exists)
    (siteFiles ++ siteFiles2) map (uri => new Site(uri)) match { 
      case Nil => defaultSite :: Nil
      case list => list
    }
  }

  def findSite(server : String, path : List[String]) : Option[(Site, List[String])] = {
    val sites = sitesByServer get(server) match {
      case Some(sites) => sites
      case None => sitesByServer get("") match {
        case Some(sites) => sites
        case None => Seq()
      }
    }
    sites find (site => path.startsWith (site.path)) match {
      case Some(site) => Some(site,path.drop(site.path.length))
      case None => None
    }
  }
}

class Site(siteFile : URI) {
  val config = new SiteConfig(siteFile)
  val locations : List[URI] = config.extent.flatMap(_.locations)
  val properties = new Properties(System.getProperties).load(siteFile)
  val name = config.name
  val server = config.server
  val path = config.path
  val contextPath = config.path.mkString("/", "/", "")
  val resourceStore = new UriStore(locations)
  val templateStore = new TemplateStore(this, resourceStore)
  val caching = false
  val components : Seq[Component] = createComponents
  val templateLocators = components flatMap (_.templateLocators.toList)
  val bindings = components flatMap (_.bindings.toList)

  val rootBinding = new RootBinding(this)
  
  lazy val emProperties = properties.parseAll("entitymanager.")
  
  def entityManagerFactory(name : String) =  
	  Persistence.createEntityManagerFactory(name, emProperties toJava)

  override def toString = {
    siteFile + List(server.emptyOrPrefix("server "), contextPath.emptyOrPrefix("path ")).trim.mkString(" (",", ", ")")
  }
  
  private def createComponents : Seq[Component] = {
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
}

class SiteConfig(siteFile : URI) {
  val configFile = siteFile.canonical
  val properties = new Properties(System.getProperties).load(siteFile)
  val name = properties("site.name")
  val server = properties("site.server")
  val parents : List[SiteConfig] = properties.list("site.parents").map(new URI(_).child("site.config")).filter(_.exists).map(new SiteConfig(_))
  val explicitLocations : List[URI] = properties.list("site.locations").map(new URI(_))
  val path : List[String] = properties("site.path").getOrElse("/").split("/").trim.toList 
  val components : List[String] = properties("site.components").split(",").trim.toList
  val location = configFile.parent
  val locations = if (!explicitLocations.isEmpty) explicitLocations else List(location)
  def id : String = name getOrElse location.toString
  def extent : List[SiteConfig] = this :: parents.flatMap(_.extent)
}