package claro.cms.components

import java.util.Locale
import xml.{Node, NodeSeq}
import net.liftweb.http.{S,SHtml}
import net.liftweb.http.js.JsCmd
import claro.cms.{Cms,FormField,FormFieldError,Component,Website,PagingBindable}
import claro.common.util.Locales
import claro.cms._
class StdComponent extends Component {

  val prefix = "cms"
  
  bindings.append {
    case component : StdComponent => Map(
      "locales" -> Website.instance.locales -> "locale",
      "paging" -> PagingBindable
    )
        
    case locale : Locale => Map(
      "short" -> locale.toString,
      "select-link" -> changeLocaleLink(locale)
    )
    
    case field : FormField => Map(
      "field" -> field.xml,
      "error" -> field.error -> "error"
    )
    
    case error : FormFieldError => Map(
      "message" -> error.message
    )
    
    case test : StringBuilder => Map(
    	"val1" -> new Binding {
    		def bind(node : Node, contxt: BindingContext) = xml.child
    	},
    	"val1" -> new Binding {
    		def bind(xml : Node, contxt: BindingContext) = xml.child
    	}
	)
  }
  
  
  def changeLocaleLink(locale : Locale) : NodeSeq => NodeSeq = { xml =>
    val prefix = "/" + Cms.locale.toString + "/"
    val uri = S.uri
    val postfix = if (uri.startsWith(prefix)) uri.substring(prefix.length) else uri
    <a href={S.hostAndPath + "/" + locale.toString + "/" + postfix}>{xml}</a> % currentAttributes()
  }
}
