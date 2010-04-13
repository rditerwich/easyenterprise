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
}

/**
 * Note the implicit conversion from a single scope to a scope list
 */
case class ResourceLocator(name : String, kind : String, scopes : Iterable[Scope]*) {
}

abstract case class Resource(val name : String, val kind : String, val scope : Scope, val locale : Locale) {
  def read : InputStream
  def write : Option[OutputStream]
  def readString = read.readString
  def readHtml = ParseHtml(read, name)._1
}

class ResourceContentCache(site : Site) {

  private final val MAX_STRING_CONTENT_SIZE = 100000;
  
  private val contentCache : ConcurrentMap[Resource,String] = 
    new com.google.common.collect.MapMaker().concurrencyLevel(32).softKeys().makeMap[Resource,String]

  def apply(resource : Resource) : String = apply(resource, resource.readString)
  
  def apply(resource : Resource, content : => String) : String = {
    contentCache.getOrElse(resource, content useIn ( content =>    	
      if (site.caching && content.size < MAX_STRING_CONTENT_SIZE) {
        contentCache.put(resource, content)
      }))
  }
}

class ResourceCache(site : Site, store : ResourceStore) {

  private val resourceCache = new ConcurrentHashMap[(ResourceLocator,Locale),Option[Resource]]()

  def apply(locator : ResourceLocator, locale : Locale) : Option[Resource] =
    if (!site.caching) store.find(locator, Locales.getAlternatives(locale)) 
    else resourceCache getOrElseUpdate ((locator,locale), store.find(locator, Locales.getAlternatives(locale)))
}

trait ResourceStore {
  
  def find(locator : ResourceLocator, locales : Seq[Locale]) : Option[Resource] = {
    for (scopes <- locator.scopes; scope <- scopes) {
      for (locale <- locales) {
        get(locator.name, locator.kind, scope, locale) match {
          case Some(resource) => Log.info("Resource found: " + mkFileName(locator.name, locator.kind, scope, locale)); return Some(resource)
          case None => Log.info("Resource not found: " + mkFileName(locator.name, locator.kind, scope, locale))

        }
      }
    }
    None
  }
  
  def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource]
  
  def mkFileName(name : String, kind : String, scope : Scope, locale : Locale) = {
    val builder = new StringBuilder(name)
    if (scope.name != "") builder.append("-").append(scope.name)
    if (scope.id != null) builder.append("-").append(scope.id)
    val localeString = locale.toString
    if (localeString != "") builder.append("-").append(localeString)
    builder.append(".").append(kind)
    builder.toString
  }
}

class FileStore(val site : Site, templateDirs : Seq[File]) extends ResourceStore {
  override def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource] = {
	val fileName = mkFileName(name, kind, scope, locale)
    for (dir <- templateDirs) {
      val resource = new File(dir, fileName)
      if (resource.exists) return Some(new Resource(name, kind, scope, locale) {
        def read = new FileInputStream(resource)
        def write = Some(new FileOutputStream(resource))
        })
	}
	None
  }
}

class ClasspathStore(val site : Site, classpath : Seq[String]) extends ResourceStore {
  override def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource] = {
    val fileName = mkFileName(name, kind, scope, locale)
    for (entry <- classpath) {
	  val resource = entry.replace('.', '/') + "/" + fileName
	  val read = () => getClass.getClassLoader.getResourceAsStream(resource)
	  val is = read()
	  try {
	    if (is != null) {
	      return Some(new Resource(name, kind, scope, locale) {
	        def read = read
	        def write = None
	        })
	    }
	  } finally {
	    is.close
	  }
    }
    None
  }
}

class UriStore(uris : Seq[URI]) extends ResourceStore {
  override def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource] = {
    val fileName = mkFileName(name, kind, scope, locale)
    for (uri <- uris) {
      val file = uri.child(fileName)
      if (file.exists) {
     	return Some(new Resource(name, kind, scope, locale) {
     	  def read = file.open get
          def write = None
          })
      }
    }
    None
  }
}

class CompoundResourceStore(stores: Seq[ResourceStore]) extends ResourceStore {
  override def find(locator : ResourceLocator, locales : Seq[Locale]) =
    stores mapFirst (_.find(locator, locales))

  override def get(name : String, kind : String, scope : Scope, locale : Locale) =
    stores mapFirst (_.get(name, kind, scope, locale))
}

