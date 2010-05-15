package claro.cms

import xml.{Node,NodeSeq}
import net.liftweb.http.{S,SHtml}
import claro.common.util.Conversions._

trait FormField {
  def error : Option[FormFieldError]
  def xml : NodeSeq
}

trait FormError {
  def message : String
}

trait FormFieldError extends FormError {
  def field : FormField
}

class Form extends Bindable {

  val Whitespace = "\\A(\\s*)\\z".r
  val EmailAddress = "\\A([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})\\z".r

  private var parent : Form = null
  var fields = List[FormField]()
  var forms = List[Form](this)
  def errors = forms.flatMap(_.fields).flatMap(_.error)
  private var currentField : Field[_] = null

  override def bindings = bindingsFor(this)
  
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    if (parent == null) {
      <lift:snippet type={"Shop:ident"} form="POST">
        { super.bind(node, context) }
      </lift:snippet>
    } else super.bind(node, context)
  }
  
  trait Field[A] extends FormField {
    fields = this :: fields
    var error: Option[FormFieldError] = None

    protected def wrap(setter : A => Any) = (value : A) => {
      error = None
      currentField = this
      setter(value)
      currentField = null
    }
  }
  
  class FieldError(val message : String, val field : FormField) extends FormFieldError


  protected def error = currentField.error.get.message
  protected def error_=(msg : String) = currentField.error = Some(new FieldError(msg, currentField)) 
  
  object TextField {
    def apply(value : => String, setter: String => Any) = new Field[String] {
      def xml = SHtml.text(value.getOrElse(""), wrap(setter)) % currentAttributes()
    }
  }
  
  object PasswordField {
    def apply(value : => String, setter: String => Any) = new Field[String] {
      def xml = SHtml.password(value.getOrElse(""), wrap(setter)) % currentAttributes()
    }
  }

  object Nested {
    def apply[A <: Form](form : A) = {
      forms = form :: forms
      form.parent = Form.this
      form
    }
  }
}

