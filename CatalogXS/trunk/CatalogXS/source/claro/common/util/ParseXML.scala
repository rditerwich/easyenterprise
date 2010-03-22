package claro.common.util

import xml.{NodeSeq,XML,Text}
import org.xml.sax.SAXParseException

object ParseXML {
  def apply(input : String) : NodeSeq = {
    try {
      println("Parsing: " + input)
    	XML.loadString("<root>\n" + input + "\n</root>").child
    } catch {
      case e : SAXParseException => {
        val lines = input.split("\n")
        if (e.getLineNumber <= lines.size) {
          
        	<span class="xml-parse-error">
           <span>{e.getMessage}:</span>
           <pre>
           {for (line <- lines.take(e.getLineNumber - 1)) yield line + "\n"}
           <b>{lines(e.getLineNumber - 1) + "\n"}</b>
           {for (line <- lines.drop(e.getLineNumber)) yield line + "\n"}
        	</pre></span>
        } else {
        	<span class="xml-parse-error"><pre>{lines}</pre></span>
        } 
      }
      case e : Throwable => Text(e.getMessage)
    }
  }
}
