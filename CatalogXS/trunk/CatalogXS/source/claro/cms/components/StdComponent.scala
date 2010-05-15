package claro.cms.components

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
