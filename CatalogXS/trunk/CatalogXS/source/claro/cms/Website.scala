package claro.cms

import xml.{Elem,NodeSeq}
import java.util.Properties
import java.io.{File,FileReader}
import java.net.URI
import javax.persistence.Persistence
import claro.common.util.SubDirs
import claro.common.util.Conversions._
import net.liftweb.util.Log

object Website {
  val defaultWebsite = new Website(new File(System.getProperty("user.home") + "/websites/default/website.config").toURI)
  val unsortedWebsites : List[Website] = findWebsites(System.getProperty("websites") getOrElse (""))
  val websites : List[Website] = unsortedWebsites.sort((s1,s2) => s1.config.path.length > s2.config.path.length)
  val websitesByServer : Map[String,Seq[Website]] = websites groupBy (_.server)
  
  def findWebsites(path : String) : List[Website] = {
    val uris : List[URI] = path split(",") filter(!_.trim.isEmpty) map(new URI(_).canonical) toList 
    val websiteFiles : List[URI] = uris flatMap (_.find(4, _.name == "website.config"))
    val websiteFiles2 : List[URI] = uris map (_.child("website.config")) filter(!websiteFiles.contains(_)) filter(_.exists)
    (websiteFiles ++ websiteFiles2) map (uri => new Website(uri)) match { 
      case Nil => defaultWebsite :: Nil
      case list => list
    }
  }

  def findWebsite(server : String, contextPath : List[String]) : Option[Website] = {
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

class Website(websiteFile : URI) {
  val config = new WebsiteConfig(websiteFile)
  val locations : List[URI] = config.extent.flatMap(_.locations)
  val name = config.name
  val server = config.server
  val contextPath = config.path
  val context = contextPath.mkString("/", "/", "")
  val websiteStores = config.extent.map(c => new UriStore(c.locations))
  val resourceStore = CompoundResourceStore(websiteStores, true)
  val resourceCache = new ResourceCache(this, resourceStore)
  val contentCache = new ResourceContentCache(this)
  val templateStore = new TemplateStore(this, resourceStore)
  val templateCache = new TemplateCache(templateStore)
  var caching = true
  val components : Seq[Component] = createComponents
  val templateLocators = components flatMap (_.templateLocators.toList)
  val bindings = components flatMap (_.bindings.toList)
  val rewrite = components flatMap (_.rewrite.toList)

  val rootBinding = new RootBinding(this)
  
  lazy val emProperties = config.properties.parseAll("entitymanager.")
  
  def entityManagerFactory(name : String) =  
	  Persistence.createEntityManagerFactory(name, emProperties toJava)

  override def toString = {
    websiteFile + List(server.emptyOrPrefix("server "), contextPath.mkString("path /", "/", "") emptyOrPrefix("path ")).trim.mkString(" (",", ", ")")
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

class WebsiteConfig(websiteFile : URI) {
  val configFile = websiteFile.canonical
  val properties = new Properties(System.getProperties).load(websiteFile)
  val parents : List[WebsiteConfig] = properties.list("website.parents").map(new URI(_).child("website.config")).filter(_.exists).map(new WebsiteConfig(_))
  for (parent <- parents) {
    properties.merge(parent.properties)
  }
  val name = properties("website.name")
  val server = properties("website.server")
  val urls : List[(String, List[String])] = properties("website.urls").split(",").trim.map(parseUrl _).toList
  val explicitLocations : List[URI] = properties.list("website.locations").map(new URI(_))
  val path : List[String] = properties("website.path").getOrElse("/").split("/").trim.toList 
  val components : List[String] = properties.list("website.components")
  val location = configFile.parent
  val locations = if (!explicitLocations.isEmpty) explicitLocations else List(location)
  def id : String = name getOrElse location.toString
  def extent : List[WebsiteConfig] = this :: parents.flatMap(_.extent)
  
  private def parseUrl(s : String) : (String, List[String]) = {
    if (s.startsWith("//")) {
      s.substring(1).split("/").toList match {
        case head :: tail => (head, tail)
        case Nil => ("", Nil)
      } 
    } else {
      s.split("/").toList match {
        case path => ("", path)
      } 
    }

  }
}