package claro.cms.components

import claro.cms.{FormField,FormFieldError,Component}

class StdComponent extends Component {

  val prefix = ""
  
  bindings.append {
    case field : FormField => Map(
      "field" -> field.xml,
      "error" -> field.error -> "error"
    )
    
    case error : FormFieldError => Map(
      "message" -> error.message
    )
  }
}
