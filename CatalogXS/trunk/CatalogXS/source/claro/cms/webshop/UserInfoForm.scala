package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import agilexs.catalogxs.jpa
import claro.common.util.Conversions._

object UserInfoForm {
  
  object NewUser extends RequestVar[UserInfoForm](new UserInfoForm(
    new jpa.party.User useIn 
      (_.setParty(new jpa.party.Party useIn 
        (_.setAddress(new jpa.party.Address))))))
  
  object ExistingUser extends RequestVar[UserInfoForm](
    WebshopModel.currentUserVar.get match {
      case Some(user) => WebshopDao.findUserById(user.getId getOrElse -1) match {
          case Some(user) => new UserInfoForm(user)
          case None => NewUser.get
        }
      case None => NewUser.get
    })
    
  def confirmUser : Boolean = {
    val email = Request.httpRequest.getParameter("email")
    val confirmationKey = Request.httpRequest.getParameter("key")
    WebshopDao.access { _ =>
      WebshopDao.findUserByEmail(email) match {
        case Some(user) => 
          if (user.getConfirmationKey == confirmationKey) {
            user.setConfirmed(true)
            true
          } else false
        case None => false
      }
    }
  }

}

class UserInfoForm(val user : jpa.party.User) extends Form {
  
  def isNew = user.getId == null
  var password = ""
  var repeatPassword = ""

  val emailField = TextField(user.getEmail, _ match {
    case Whitespace(s) => error = "No email address specified"; user.setEmail("")
    case EmailAddress(s) => if (isNew && emailExists(s)) error = "Email address is already in use"; user.setEmail(s)
    case s => error = "Invalid email address"; user.setEmail(s)
  })
  val passwordField = PasswordField(password, _ match {
    case Whitespace(s) if isNew => error = "Password must not be empty"; password = ""
    case s => password = s
  })
  val repeatPasswordField = PasswordField(repeatPassword, _ match {
    case s if (password != s) => error = "Passwords do not match"; repeatPassword = ""
    case s => repeatPassword = s
  })
  val partyForm = Nested(PartyForm(user.getParty))
  
  def emailExists(email : String) = WebshopDao.findUserByEmail(email) != None 

  def storeButton(label : String, href : String, confirmHref : String) = SHtml.submit(label, () => store(href, confirmHref)) % currentAttributes()
  
  def store(href : String, confirmHref : String) = {

    // store user when there are no validation errors
    if (errors.isEmpty) {
      
      // store password?
      if (password != "" && password != user.getPassword) {
        user.setPassword(WebshopModel.encryptPassword(password))
        user.setConfirmed(false)
        
        val key = "1234"
        
        val req = Request.httpRequest
        
        val msg = "Dear " + user.getParty.getName + ",\n\n" +
          "Thank you for registering with " + Request.website.name + ".\n\n" + 
          "Please, select the following link to activate your account:\n\n" + 
          req.getScheme + "://" + req.getServerName + ":" + req.getServerPort + req.getContextPath + confirmHref + "?email=" + user.getEmail + "&key=" + key;
        
        Mail.mail(Subject("Confirm " + Request.website.name + " registration"),
                  To(user.getEmail),
                  PlainMailBodyType(msg))
      }

      // store user
      WebshopDao.access (_.merge(user))
      WebshopModel.currentUserVar.set(Some(user))
      if (href.trim != "") {
        S.redirectTo(href)
      }
    }
    else {
      if (isNew) {
        UserInfoForm.NewUser.set(this)
      } else {
        UserInfoForm.ExistingUser.set(this)
      }
    }
  }
}


