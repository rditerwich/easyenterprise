package claro.common.util

import claro.common.util.Conversions._
import java.io.{File,FileInputStream,InputStream}
import java.net.URI
import collection.mutable.ArrayBuffer

object RichUri {

  private def scan(uri : URI, depth : Int, pred : URI => Boolean, result : ArrayBuffer[URI]) : ArrayBuffer[URI] = {
    if (depth >= 0) {
      if (pred(uri)) result += uri
      for (suburi <- new RichUri(uri).list) {
        scan(suburi, depth - 1, pred, result)
      }
    }
    result
  }
}

class RichUri(u : URI) {

  val uri = u.getScheme match {
    case null => new File(u.getPath).getCanonicalFile.toURI
//    case "classpath" => new URI("classpath", if (u.isOpaque) "/" + u.getSchemeSpecificPart.replace('.','/') else u.getPath, null) 
    case scheme => if (u.isOpaque) new URI(scheme, "/" + u.getSchemeSpecificPart.replace('.','/'), null) else u
  }
  
  def canonical = uri
  
  def path = uri.getPath
  
  def name : String = path.dropSuffix("/").afterLast('/')
  
  def parent : URI = uri.resolve(if (path.endsWith("/")) ".." else ".")
  
  def child(childPath : String) = uri.resolve(if (path.endsWith("/")) childPath.dropPrefix("/") else name + childPath.ensurePrefix("/")) 

  def find(pred : URI => Boolean) : Seq[URI] = find(0, pred)
  
  def find(depth : Int, pred : URI => Boolean) : Seq[URI] = RichUri.scan(uri, depth, pred, new ArrayBuffer[URI])

  def list : Seq[URI] = uri.getScheme match {
    case "file" => new File(uri).listFiles match {
      case null => Seq()
      case files => files map (_.toURI)
    }
    case "classpath" => Seq()
    case _ => Seq()
  }
  
  def open : Option[InputStream] = uri.getScheme match {
    case "file" => 
      val file = new File(uri)
      if (file.exists) Some(new FileInputStream(file))
      else None
    case "classpath" => getClass.getClassLoader.getResourceAsStream(path.dropPrefix("/")) match {
      case null => None
      case is => Some(is)
    }
    case _ => tryOrElse(Some(uri.toURL.openStream), None)
  } 
  
  def exists : Boolean = uri.getScheme match {
    case "file" => new File(uri).exists
    case _ => tryOrElse(open match { case Some(is) => is.close; true; case None => false }, false)
  }
  
  private def tryOrElse[A](f : => A, orElse : => A) : A = {
    try {
	    f
    } catch {
      case _ => orElse
    }
  }
}
