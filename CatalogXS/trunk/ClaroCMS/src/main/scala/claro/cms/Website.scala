package claro.cms

import java.util.{Locale,Properties}
import java.io.{File,FileReader}
import java.net.URI
import javax.persistence.Persistence
import xml.{Elem,NodeSeq}
import net.liftweb.util.Log
import claro.common.util.{SubDirs,Locales}
import claro.common.util.Conversions._

object Website {
//  val defaultWebsite = new Website(new File(System.getProperty("user.home") + "/websites/default/website.config").toURI)
//  val unsortedWebsites : List[Website] = findWebsites(System.getProperty("websites") getOrElse (""))
//  val websites : Seq[Website] = unsortedWebsites.sort((s1,s2) => s1.config.path.length > s2.config.path.length)
//  val websitesByServer : scala.collection.Map[String,Seq[Website]] = websites.groupBy (_.server) 
  
  var instance : Website = null
  
  
//  def findWebsites(path : String) : List[Website] = {
//    val uris : List[URI] = path split(",") filter(!_.trim.isEmpty) map(new URI(_).canonical) toList 
//    val websiteFiles : List[URI] = uris flatMap (_.find(4, _.name == "website.config"))
//    val websiteFiles2 : List[URI] = uris map (_.child("website.config")) filter(!websiteFiles.contains(_)) filter(_.exists)
//    (websiteFiles ++ websiteFiles2) map (uri => new Website(uri)) match { 
//      case Nil => defaultWebsite :: Nil
//      case list => list
//    }
//  }
  
  def register(websiteUri : String) = {
    val uri = new URI(websiteUri).canonical.child("website.config")
    if (!uri.exists) {
      throw new Exception("Website configuration file not found:'" + uri)
    }
    instance = new Website(uri)
  }
  
//  def find(server : String, contextPath : List[String]) : Option[Website] = {
//    val websites = websitesByServer get(server) match {
//      case Some(websites) => websites
//      case None => websitesByServer get("") match {
//        case Some(websites) => websites
//        case None => Seq()
//      }
//    }
//    websites find (website => contextPath.startsWith (website.contextPath))
//  }
}

class Website(websiteFile : URI) {
  val config = new WebsiteConfig(websiteFile)
  val locations : List[URI] = config.extent.flatMap(_.locations)
  val name = config.name
  val server = config.server
  val contextPath = config.path
  val context = contextPath.mkString("/", "/", "")
  val components : Seq[Component] = createComponents
  val templateLocators = components flatMap (_.templateLocators.toList)
  val websiteStores = config.extent.map(c => new UriStore(c.locations))
  val resourceStore = CompoundResourceStore(websiteStores, true)
  val resourceCache = new ResourceCache(resourceStore)
  val contentCache = new ResourceContentCache
  val templateStore = new TemplateStore(templateLocators, resourceStore)
  val templateCache = new TemplateCache(templateStore)
  val bindings = components flatMap (_.bindings.toList)
  val rewrite = components flatMap (_.rewrite.toList)
  val dispatch = components flatMap (_.dispatch.toList)
  val locales = config.locales.map(Locales(_))
  val defaultLocale = Locales(config.defaultLocale) 

  val rootBinding = new RootBinding(this)
  
  lazy val emProperties = config.properties.parseAll("entitymanager.")
  
  Dao.setProperties(emProperties)
  
  /**
   * @deprecated
   */
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

  def printInfo(prefix : String) = "\n" + prefix + "Web site: " +
    config.name.emptyOrPrefix("\n" + prefix + "  Name: ") + 
    server.emptyOrPrefix("\n" + prefix + "  Server: ") + 
    context.emptyOrPrefix("\n" + prefix + "  Context: ") + 
    (config.parents.map(_.id) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Parent: " + single
      case many => "\n" + prefix + "  Parents:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) + 
    (locations.map(_.toString) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Location: " + single
      case many => "\n" + prefix + "  Locations:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) + 
    (emProperties.map(e => e._1 + "=" + e._2) match {
    case Nil => ""
    case many => "\n" + prefix + "  Entity Manager:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) 
}

class WebsiteConfig(val uri : URI) {
  val configFile = uri.canonical
  val properties = new Properties(System.getProperties).load(configFile)
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
  val locales = properties.list("website.locales").toSet
  val defaultLocale = properties("website.defaultLocale", "")
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