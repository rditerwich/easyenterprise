package claro.cms.showcase

import scala.xml.NodeSeq
import claro.common.util.Conversions._

class ShowcaseComponent extends Component {

  val prefix = "showcase"

  bindings.append {
    case _ : ShowcaseComponent => Map(
      "sample" -> sample _)
    
  }

  def sample(xml : NodeSeq) = {
    <tr><td><pre>{ xml.format }</pre></td>
    <td>{ xml }</td></tr>
  }

}
