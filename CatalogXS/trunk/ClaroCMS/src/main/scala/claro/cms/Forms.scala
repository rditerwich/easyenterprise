package claro.cms

import collection.mutable
import xml.{Node,NodeSeq}
import net.liftweb.http.{S,SHtml}
import net.liftweb.http.js.{JsCmd,JsCmds}
import claro.common.util.Conversions._

trait FormField extends Bindable {
	override val defaultPrefix = "field"
  def error : Option[FormFieldError]
  def xml : NodeSeq
}

trait FormError extends Bindable {
	override val defaultPrefix = "error"
  def message : String
}

trait FormFieldError extends FormError {
  def field : FormField
}

class Form extends Bindable {

  val Whitespace = "\\A(\\s*)\\z".r
  val EmailAddress = "\\A([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})\\z".r

  private var parent : Form = null
  val fields = mutable.ArrayBuffer[FormField]()
  val forms = mutable.ArrayBuffer[Form](this)
  val formErrors = mutable.ArrayBuffer[FormError]()
  def errors = forms.flatMap(_.fields).flatMap(_.error) ++ formErrors
  private var currentField : Field[_] = null

  override val defaultPrefix = "form"
  val formId = getClass.getName
  
  override def bind(node : Node, context : BindingContext) : NodeSeq = {
    if (parent == null) {
//    	SHtml.ajaxForm(super.bind(node, context))
      <form method="post" action={S.uri} id={formId} class="form">
        { super.bind(node, context) }
      </form> 
    } else super.bind(node, context)
  }
  
  trait Field[A] extends FormField {
    fields + this
    var error: Option[FormFieldError] = None

    protected def wrap(setter : A => Any) = (value : A) => {
      error = None
      currentField = this
      setter(value)
      currentField = null
    }
  }
  
  class Error(val message : String) extends FormError
  class FieldError(val message : String, val field : FormField) extends FormFieldError


  protected def error = currentField.error.get.message
  protected def error_=(msg : String) = 
  	if (currentField != null) currentField.error = Some(new FieldError(msg, currentField))
  	else formErrors + new Error(msg)
  
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
  
  object Submit {
  	def apply(label : String)(f : => Any) = (xml : NodeSeq) => SHtml.submit(label, {formErrors.clear; () => f}) % currentAttributes()
  }

  object Nested {
    def apply[A <: Form](form : A) = {
      forms + form
      form.parent = Form.this
      form
    }
  }
}

