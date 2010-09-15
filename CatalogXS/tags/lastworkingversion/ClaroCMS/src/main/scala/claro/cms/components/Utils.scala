package claro.cms.components

import xml.{Elem,Group,Node,NodeSeq,Text,MetaData}
import claro.cms.{Bindable,Bindings,BindingContext,Component}

class Utils extends Component {

  val prefix = "util"
  
  bindings.append {
    case _ : Utils => Map(
      "break" -> new Break(@@("size", 80).toInt))
    
  }
}


class Break(size : Int) extends Bindable {
  override def bind(node : Node, context : BindingContext) = {
    val result = super.bind(node, context + ("break" -> Bindings(null, Map("more" -> NodeSeq.Empty)))).toString
    if (result.size > size) {
      val more = node.child.find(child => child.prefix == "break" && child.label == "more") getOrElse Text("") 
      Text(result.substring(0, size)) ++ super.bind(more, context)
    } else {
      Text(result)
    }
  }
}
