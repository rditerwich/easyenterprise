package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import xml.{Node, NodeSeq, MetaData}
import claro.jpa
import claro.cms.{Form,Mail,Website}

object LoginForm extends RequestVar[LoginForm](new LoginForm)

class LoginForm extends Form {
  var email : String = ""
  var password : String = ""
  
  val emailField = TextField(email, _ match {
    case Whitespace(s) => error = "No email address specified"; email = ""
    case EmailAddress(s) => email = s
    case s => error = "Invalid email address"; email = s
  })

  val passwordField = PasswordField(password, _ match {
    case Whitespace(s) => error = "Password must not be empty"; password = ""
    case s => password = s
  })

  val loginButton = Submit("Login") {
  	if (errors.isEmpty) {
	    WebshopDao.findUserByEmailAndPassword(email, ChangePasswordForm.encrypt(password)) match {
	      case Some(user) => WebshopModel.currentUserVar.set(Some(user))
	      case None => error = "Login failed"
	    }
  	} 
  	
  	if (!errors.isEmpty) {
  		// keep error messages
      LoginForm(this)
  	}
  }
  
  override lazy val bindings = Map(
    "email-field" -> emailField,
    "password-field" -> passwordField,
    "errors" -> errors,
    "form-errors" -> formErrors,
    "login-button" -> loginButton)
}
