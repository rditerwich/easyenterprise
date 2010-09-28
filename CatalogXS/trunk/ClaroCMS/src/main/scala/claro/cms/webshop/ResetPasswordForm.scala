package claro.cms.webshop

import claro.cms.{Form, Mail, Website}
import claro.cms.jscmds.ReloadPage
import net.liftweb.http.{S,SHtml, RequestVar}
import net.liftweb.http.js.{JsCmd,JsCmds}
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}

object ResetPasswordForm extends RequestVar[ResetPasswordForm](new ResetPasswordForm) 

class ResetPasswordForm extends Form {
  var email : String = ""

  val href = @@("href") 
  val changePasswordHref = @@("change-password-href")
  
  val emailField = TextField(email, _ match {
    case Whitespace(s) => error = "No email address specified"; email = ""
    case EmailAddress(s) => email = s
    case s => error = "Invalid email address"; email = s
  })
  
  val submitButton = Submit("Reset") {
  	if (errors.isEmpty) {
	    Mail.mail(Subject("Set Password " + Website.instance.name),
	      To(email),
	      PlainMailBodyType("Please use the following link to set your password and activate your account:\n\n" +
	      ChangePasswordForm.createLink(changePasswordHref, email)))
	      S.redirectTo(href)
  	} else {
  		// keep error messages
      ResetPasswordForm(this)
  	}
  }
  
  override lazy val bindings = Map(
  		"email-field" -> emailField,
  		"submit-button" -> submitButton
		)
}
