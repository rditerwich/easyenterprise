package claro.common.util

import java.io.InputStream
import xml.{NodeSeq,XML,Text}
import org.xml.sax.SAXParseException

object ParseXML {
  def apply(input : InputStream) : NodeSeq = {
      try {
    	  getClass.getClassLoader.getResourceAsStream(resource) match {
    	    case null => null
            case is => XML.load(is)
    	  }
      } catch {
        case e => println("Error reading resource: " + e); throw e
      }
  }
  
  def apply(input : String) : NodeSeq = {
    try {
      println("Parsing: " + input)
    	XML.loadString("<root>\n" + input + "\n</root>").child
    } catch {
      case e : SAXParseException => {
        val lines = input.split("\n")
        val index = e.getLineNumber - 2
        if (index <  lines.size) {
          
        	<span class="xml-parse-error">
           <span>{e.getMessage}</span>
           <pre>{for (line <- lines.take(index)) yield line + "\n"
           }<b>{lines(index) + "\n"}</b>{
           for (line <- lines.drop(index + 1)) yield line + "\n"}</pre></span>
        } else {
        	<span class="xml-parse-error"><pre>{lines}</pre></span>
        } 
      }
      case e : Throwable => Text(e.getMessage)
    }
  }
}
