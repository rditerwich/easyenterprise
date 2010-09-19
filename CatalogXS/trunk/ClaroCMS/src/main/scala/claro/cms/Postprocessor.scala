package claro.cms

import xml._

object Postprocessor {

	def postprocess(xml : NodeSeq) : NodeSeq = xml flatMap {
    case s : Elem =>
    	val (childNodes, attrNodes) = s.child.partition(_.prefix != "attr")
    	if (attrNodes.isEmpty) Elem(s.prefix, s.label, s.attributes, s.scope, postprocess(childNodes) :_*)
    	else {
        val attrs : MetaData = attrNodes.foldLeft(s.attributes)((attrs, attrNode) => {
        	val name = attr(attrNode, "name")
        	val value = attr(attrNode, "value")
        	attrNode.label match {
	        	case "set" => new UnprefixedAttribute(name, value, attrs.filter(_.key != name))
	        	case "append" => new UnprefixedAttribute(name, attrs.find(_.key == name).map(_.value).mkString("", "", " ") + value, attrs.filter(_.key != name))
	        	case "clear" => attrs.filter(_.key != name)
	        	case _ => attrs
	        }
        })
        Elem(s.prefix, s.label, attrs, s.scope, postprocess(childNodes) :_*)
    	}
    case Group(nodes) => Group(postprocess(nodes))
    case n => n
  }
	
  def attr(node : Node, name : String) : String = 
    node.attributes.find(at => at.key == name && !at.isPrefixed) match {
      case Some(attr) => attr.value.toString
      case None => "some-attr"
    }

}