package claro.cms.webshop

import net.liftweb.http.{S,SHtml,SessionVar}
import xml.{Node, NodeSeq, MetaData}

object LoginForm extends SessionVar[LoginForm](new LoginForm)

class LoginForm extends BindableForm {
  val dummyEmail = "email"
  val dummyPassword = ""
  var email : String = dummyEmail
  var password : String = dummyPassword
  
  val pathPrefix = WebshopModel.currentProductGroup match {
    case Some(group) => "/group/" + group.id
    case None => ""
  }
  
  def emailField = SHtml.text(email, email = _, 
      ("onclick", "javascript:if (value == '" + dummyEmail + "') value = '';"), 
      ("onblur", "javascript:if (value == '') value = '" + dummyEmail + "';")) % currentAttributes()
    
  def passwordField = SHtml.password(password, password= _, 
        ("onclick", "javascript:if (value == '" + dummyPassword + "') value = ''"), 
        ("onblur", "javascript:if (value == '') value = '" + dummyPassword + "';")) % currentAttributes()
        
  def loginButton = SHtml.submit("Login", () => S.redirectTo(pathPrefix + "/search/" + email)) % currentAttributes()
}