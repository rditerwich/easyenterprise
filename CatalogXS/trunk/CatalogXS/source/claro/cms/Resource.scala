package claro.cms

import xml.NodeSeq
import java.io.{File,FileInputStream,FileOutputStream,InputStream,OutputStream}
import java.util.Locale
import java.net.URI
import claro.cms.util.ParseHtml
import claro.common.util.Conversions._

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

class Resource(val name : String, val kind : String, val scope : Scope, val locale : Locale, read : () => InputStream, write : () => OutputStream) {
  def readHtml : NodeSeq = ParseHtml(read(), name)._1 
}

trait ResourceStore {
  
  def find(locator : ResourceLocator, locales : Seq[Locale]) : Option[Resource] = {
    for (scopes <- locator.scopes; scope <- scopes) {
      for (locale <- locales) {
        get(locator.name, locator.kind, scope, locale) match {
          case Some(resource) => return Some(resource)
          case None =>
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

class FileStore(templateDirs : Seq[File]) extends ResourceStore {
  override def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource] = {
	val fileName = mkFileName(name, kind, scope, locale)
    for (dir <- templateDirs) {
      val resource = new File(dir, fileName)
//      println("Looking for template: " + resource)
      if (resource.exists) return Some(new Resource(name, kind, scope, locale, () => new FileInputStream(resource), () => new FileOutputStream(resource)))
	}
	None
  }
}

class ClasspathStore(classpath : Seq[String]) extends ResourceStore {
  override def get(name : String, kind : String, scope : Scope, locale : Locale) : Option[Resource] = {
    val fileName = mkFileName(name, kind, scope, locale)
    for (entry <- classpath) {
	  val resource = entry.replace('.', '/') + "/" + fileName
	  val read = () => getClass.getClassLoader.getResourceAsStream(resource)
	  val is = read()
	  try {
	    if (is != null) {
	      return Some(new Resource(name, kind, scope, locale, read, null))
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
     	return Some(new Resource(name, kind, scope, locale, () => file.open get, null))
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

