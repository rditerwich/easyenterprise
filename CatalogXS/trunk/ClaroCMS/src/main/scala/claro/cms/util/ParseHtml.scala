package claro.cms.util

import java.io.InputStream
import xml.{NodeSeq,XML,Text}
import xml.parsing.{NoBindingFactoryAdapter}
import org.xml.sax.{SAXParseException,InputSource}
import claro.common.util.Conversions._

/**
 * Loads XHTML data and returns parse errors as an html structure embedded in the
 * result.
 */
object ParseHtml {
  
  def apply(getInputStream : => InputStream, publicId : String) : (NodeSeq,Option[Throwable]) = {
    try {
      val input = getInputStream.readString 
      try {
        (XML.loadString("<root>\n" + input + "\n</root>").child, None)
      } catch {
        case e : SAXParseException => (mkError(io.Source.fromString(input), e, 1),Some(e))
      }
    } catch { 
      case e  =>
        println("ERROR:"+e)
        (Text("Error in " + publicId + ": " + e.getMessage),Some(e))
    }
  }
  
  def apply(input : String) : NodeSeq = {
    try {
      XML.loadString("<root>\n" + input + "\n</root>").child
    } catch {
      case e : SAXParseException => mkError(io.Source.fromString(input), e, 1) 
      case e : Throwable => Text(e.getMessage)
    }
  }
  
  def mkError(input : io.Source, e : SAXParseException, corr : Int) : NodeSeq = {
		  val lines = Array(input.getLines().toList:_*)
		  val index = e.getLineNumber - 1 - corr
		  if (index < lines.size) {
			  <span class="xml-parse-error">
			  <span>{e.getMessage}</span>
			  <pre>{for (line <- lines.take(index)) yield line
			  }<b>{lines(index)}</b>{
				  for (line <- lines.drop(index + 1)) yield line}</pre></span>
		  } else {
			  <span class="xml-parse-error">
			  <span>{e.getMessage}</span>
			  <pre>{lines}</pre></span>
		  } 
  }
}
