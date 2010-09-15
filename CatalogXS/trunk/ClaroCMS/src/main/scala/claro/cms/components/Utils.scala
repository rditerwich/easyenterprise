package claro.cms.components

import xml.{Elem,Group,Node,NodeSeq,Text,MetaData}
import claro.cms.{Binding,Bindings,Bindable,BindingContext,Component}

class Utils extends Component {

  val prefix = "util"
  
  bindings.append {
    case _ : Utils => Map(
      "break" -> new Break(@@("size", 80).toInt))
    
  }
}

class Break(size : Int) extends Bindable {
  override def bind(node : Node, context : BindingContext) = {
    val result = Binding.bind(node.child, context + ("break" -> Bindings(None, Map("more" -> NodeSeq.Empty)))).toString
    if (result.size > size) {
      val more = node.child.find(child => child.prefix == "break" && child.label == "more") getOrElse Text("") 
      Text(result.substring(0, size)) ++ Binding.bind(more, context)
    } else {
      Text(result)
    }
  }
}
