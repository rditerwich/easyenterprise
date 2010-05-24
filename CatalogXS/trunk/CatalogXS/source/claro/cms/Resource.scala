package claro.cms

import xml.NodeSeq
import java.io.{File,FileInputStream,FileOutputStream,InputStream,OutputStream}
import java.util.Locale
import java.util.concurrent.{ConcurrentMap,ConcurrentHashMap,TimeUnit}
import java.net.URI
import claro.cms.util.ParseHtml
import claro.common.util.Locales
import claro.common.util.Conversions._
import net.liftweb.util.Log

object Scope {
  def apply(id : Any) = new Scope("", id)
  def apply(t : (String,Any)) = new Scope(t._1,t._2)
  val global = new Scope("",null) 
}

case class Scope (name : String, id : Any) {
}

object ResourceLocator {
  def apply(path : List[String], kind : String, scopes : Iterable[Scope]*) = 
    new ResourceLocator(path, kind, scopes.flatMap(a => a))
}

/**
 * Note the implicit conversion from a single scope to a scope list
 */
case class ResourceLocator(path : List[String], kind : String, scopes : Iterable[Scope]) {
}

abstract case class Resource(val path : List[String], val kind : String, val scope : Scope, val locale : Locale) {
  def readStream : InputStream
  def writeStream : Option[OutputStream]
  def readString = readStream.readString
  def readBytes = readStream.readBytes
  def exists : Boolean
  def uri : URI
}

class ResourceContentCache(website : Website) {

  private final val MAX_CONTENT_SIZE = 200000;
  
  private val contentCache : ConcurrentMap[Resource,Array[Byte]] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).softKeys().makeMap[Resource,Array[Byte]]

  def apply(resource : Resource) : Array[Byte] = apply(resource, resource.readBytes)
  
  def apply(resource : Resource, content : => Array[Byte]) : Array[Byte] = {
    contentCache.getOrElse(resource, content useIn ( content =>    	
      if (website.caching && content.size < MAX_CONTENT_SIZE) {
        contentCache.put(resource, content)
      }))
  }
}

class ResourceCache(website : Website, store : ResourceStore) {

  private val resourceCache = new ConcurrentHashMap[(ResourceLocator,Locale),Option[Resource]]()

  def apply(locator : ResourceLocator, locale : Locale) : Option[Resource] =
    if (website.caching) {
      resourceCache.get((locator,locale)) match {
        case null => 
          val resource = store.find(locator, Locales.getAlternatives(locale))
          resourceCache.put((locator,locale), resource)
          resource
        case resource => resource
      }
    }
    else store.find(locator, Locales.getAlternatives(locale)) 
}

trait ResourceStore {
  def find(locator : ResourceLocator, locales : Seq[Locale]) : Option[Resource] = {
    for (scope <- locator.scopes) {
      for (locale <- locales) {
        for (resource <- get(locator.path, locator.kind, scope, locale)) {
          Log.info("Scanning resource: " + resource.uri); 
          if (resource.exists) {
            Log.info("Resource found: " + resource.uri); 
            return Some(resource)
          }
        }
      }
    }
    None
  }
  
  def get(path : List[String], kind : String, scope : Scope, locale : Locale) : Seq[Resource]
  
  def mkFileName(path : List[String], kind : String, scope : Scope, locale : Locale) = {
    val builder = new StringBuilder()
    path.addString(builder, "/")
    if (scope.name != "") builder.append("-").append(scope.name)
    if (scope.id != null) builder.append("-").append(scope.id)
    val localeString = locale.toString
    if (localeString != "") builder.append("_").append(localeString)
    builder.append(".").append(kind)
    builder.toString
  }
}

class FileStore(dirs : Seq[File]) extends ResourceStore {
  override def get(path : List[String], kind : String, scope : Scope, locale : Locale) : Seq[Resource] = {
  	val fileName = mkFileName(path, kind, scope, locale)
    dirs map { dir =>
      val file = new File(dir, fileName)
      new Resource(path, kind, scope, locale) {
        def readStream = new FileInputStream(file)
        def writeStream = Some(new FileOutputStream(file))
        def uri = file.toURI
        def exists = file.exists
      }
    }
  }
}

class ClasspathStore(val website : Website, classPath : Seq[String]) extends ResourceStore {
  override def get(path : List[String], kind : String, scope : Scope, locale : Locale) : Seq[Resource] = {
    val fileName = mkFileName(path, kind, scope, locale)
    classPath map { pkg =>
  	  val resource = pkg.replace('.', '/') + "/" + fileName
      new Resource(path, kind, scope, locale) {
        def readStream = getClass.getClassLoader.getResourceAsStream(resource)
        def writeStream = None
        def uri = new URI("classpath", resource, null)
        def exists = readStream.exists
      }
    }
  }
}

class UriStore(uris : Seq[URI]) extends ResourceStore {
  override def get(path : List[String], kind : String, scope : Scope, locale : Locale) : Seq[Resource] = {
    val fileName = mkFileName(path, kind, scope, locale)
    uris map { uri =>
      val file = uri.child(fileName)
     	new Resource(path, kind, scope, locale) {
     	  def readStream = file.open get
        def writeStream = None
        def uri = file
        def exists = file.exists
      }
    }
  }
}

object CompoundResourceStore {
  def apply(stores : Seq[ResourceStore], localFirst : Boolean) =
    if (stores.size == 1) stores(0)
    else new CompoundResourceStore(stores, localFirst)
}

class CompoundResourceStore(stores: Seq[ResourceStore], localFirst : Boolean) extends ResourceStore {
  override def find(locator : ResourceLocator, locales : Seq[Locale]) =
    if (localFirst) stores mapFirst (_.find(locator, locales))
    else super.find(locator, locales)

  override def get(path : List[String], kind : String, scope : Scope, locale : Locale) =
    stores flatMap (_.get(path, kind, scope, locale))
}

