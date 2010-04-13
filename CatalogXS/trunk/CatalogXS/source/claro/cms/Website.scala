package claro.cms

import xml.{Elem,NodeSeq}
import java.util.Properties
import java.io.{File,FileReader}
import java.net.URI
import javax.persistence.Persistence
import claro.common.util.SubDirs
import claro.common.util.Conversions._
import net.liftweb.util.Log

object Webwebsite {
  val defaultWebwebsite = new Webwebsite(new File(System.getProperty("user.home") + "/websites/default/website.config").toURI)
  val unsortedWebwebsites : List[Webwebsite] = findWebwebsites(System.getProperty("websites") getOrElse (""))
  val websites : List[Webwebsite] = unsortedWebwebsites.sort((s1,s2) => s1.config.path.length > s2.config.path.length)
  val websitesByServer : Map[String,Seq[Webwebsite]] = websites groupBy (_.server)
  
  def findWebwebsites(path : String) : List[Webwebsite] = {
    val uris : List[URI] = path split(",") filter(!_.trim.isEmpty) map(new URI(_).canonical) toList 
    val websiteFiles : List[URI] = uris flatMap (_.find(4, _.name == "website.config"))
    val websiteFiles2 : List[URI] = uris map (_.child("website.config")) filter(!websiteFiles.contains(_)) filter(_.exists)
    (websiteFiles ++ websiteFiles2) map (uri => new Webwebsite(uri)) match { 
      case Nil => defaultWebwebsite :: Nil
      case list => list
    }
  }

  def findWebwebsite(server : String, contextPath : String) : Option[Webwebsite] = {
    val websites = websitesByServer get(server) match {
      case Some(websites) => websites
      case None => websitesByServer get("") match {
        case Some(websites) => websites
        case None => Seq()
      }
    }
    websites find (website => contextPath.startsWith (website.contextPath))
  }
}

class Webwebsite(websiteFile : URI) {
  val config = new WebwebsiteConfig(websiteFile)
  val locations : List[URI] = config.extent.flatMap(_.locations)
  val properties = new Properties(System.getProperties).load(websiteFile)
  val name = config.name
  val server = config.server
  val path = config.path
  val contextPath = config.path.mkString("/", "/", "")
  val resourceStore = new UriStore(locations)
  val resourceCache = new ResourceCache(this, resourceStore)
  val contentCache = new ResourceContentCache(this)
  val templateStore = new TemplateStore(this, resourceStore)
  val templateCache = new TemplateCache(templateStore)
  val caching = false
  val components : Seq[Component] = createComponents
  val templateLocators = components flatMap (_.templateLocators.toList)
  val bindings = components flatMap (_.bindings.toList)

  val rootBinding = new RootBinding(this)
  
  lazy val emProperties = properties.parseAll("entitymanager.")
  
  def entityManagerFactory(name : String) =  
	  Persistence.createEntityManagerFactory(name, emProperties toJava)

  override def toString = {
    websiteFile + List(server.emptyOrPrefix("server "), contextPath.emptyOrPrefix("path ")).trim.mkString(" (",", ", ")")
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

class WebwebsiteConfig(websiteFile : URI) {
  val configFile = websiteFile.canonical
  val properties = new Properties(System.getProperties).load(websiteFile)
  val name = properties("website.name")
  val server = properties("website.server")
  val parents : List[WebwebsiteConfig] = properties.list("website.parents").map(new URI(_).child("website.config")).filter(_.exists).map(new WebwebsiteConfig(_))
  val explicitLocations : List[URI] = properties.list("website.locations").map(new URI(_))
  val path : List[String] = properties("website.path").getOrElse("/").split("/").trim.toList 
  val components : List[String] = properties("website.components").split(",").trim.toList
  val location = configFile.parent
  val locations = if (!explicitLocations.isEmpty) explicitLocations else List(location)
  def id : String = name getOrElse location.toString
  def extent : List[WebwebsiteConfig] = this :: parents.flatMap(_.extent)
}