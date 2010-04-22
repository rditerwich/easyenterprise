package claro.cms.components

import xml.{Elem,Group,Node,NodeSeq,Text,MetaData}

class Utils extends Component {

  val prefix = "util"
  
  bindings.append {
    case _ : Utils => Map(
      "sample" -> sample _)
    
  }

  def sample(xml : NodeSeq) = {
    <tr><td><pre>{ XmlFormatter(xml) }</pre></td>
    <td>{ xml }</td></tr>
  }
}

object XmlFormatter {

  def apply(xml : NodeSeq) : NodeSeq = {
    val replace = xml.toList.head match {
      case text : Text if text._data.contains('\n') =>
        val ws = "\\s*".r findPrefixOf text._data
        "\n\\s*".r findPrefixOf text._data match {
          case Some(prefix) => (s : String) => s.replaceFirst(prefix, "\n")
          case None => (s : String) => s
        }
      case _ => (s : String) => s
    }
    format(xml, replace)
  }
  
  def format(xml : NodeSeq, replace : String => String) : NodeSeq = {
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
