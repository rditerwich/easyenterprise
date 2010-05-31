package claro.common.util

import xml.{Node,NodeSeq,Elem,Text,MetaData,Group}

class RichNodeSeq(xml : NodeSeq) {

  def find(prefix : String, label : String) : NodeSeq = find(xml, prefix, label)
  
  private def find(xml : NodeSeq, prefix : String, label : String) : NodeSeq = {
    xml flatMap {
      case s : Elem if ((prefix == null || prefix == s.prefix) && label == s.label) => Seq(s) 
      case s : Elem => find(s.child, prefix, label) 
      case Group(nodes) => find(nodes, prefix, label)
      case _ => Seq.empty
    }
  }
  
  def exists(prefix : String, label : String) : Boolean = exists(xml, prefix, label)
  
  private def exists(xml : NodeSeq, prefix : String, label : String) : Boolean = {
    for (node <- xml) node match {
      case s : Elem if ((prefix == null || prefix == s.prefix) && label == s.label) => return true 
      case s : Elem => if (exists(s.child, prefix, label)) return true 
      case Group(nodes) => if (exists(nodes, prefix, label)) return true
      case _ => 
    }
    false
  }
  

  def format : NodeSeq = {
    val (xml2,replace) = xml.toList.head match {
      case text : Text if text._data.contains('\n') =>
        val ws = "\\s*".r findPrefixOf text._data
        "\n\\s*".r findPrefixOf text._data match {
          case Some(prefix) => (xml.drop(1), (s : String) => s.replaceFirst(prefix, "\n"))
          case None => (xml, (s : String) => s)
        }
      case _ => (xml, (s : String) => s)
    }
    format(xml2, replace)
  }
  
  private def format(xml : NodeSeq, replace : String => String) : NodeSeq = {
    xml flatMap {
      case s : Elem =>
        def label = if (s.prefix == null) Text(s.label) else purple2(Text(s.prefix + ":" + s.label))
        def purple(xml : NodeSeq) = <span style="color:#881280">{xml}</span>
        def purple2(xml : NodeSeq) = <span style="color:#881280">{xml}</span>
        def orange(xml : NodeSeq) = <span style="color:#994500">{xml}</span>
        def blue(xml : NodeSeq) = <span style="color:#1A1AA6">{xml}</span>
        def attr(attr: MetaData) = <span> {orange(Text(attr.key)) ++ purple(Text("=\"")) ++ blue(attr.value) ++ purple(Text("\""))}</span>
        def attrs = s.attributes.map(attr _)
        
        if (s.child.isEmpty) purple(Text("<") ++ label ++ attrs ++ Text("/>")) 
        else purple(Text("<") ++ label ++ attrs ++ Text(">")) ++ format(s.child, replace) ++ purple2(Text("</") ++ label ++ Text(">")) 
      case Group(nodes) => Group(format(nodes, replace))
      case Text(text) => Text(replace(text))
      case n => n
    }
    
  }

}
