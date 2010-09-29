package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import xml.NodeSeq
import claro.common.util.Conversions._
import claro.jpa
import claro.cms.Form
import java.security.MessageDigest
import java.nio.charset.Charset

object UserInfoForm extends RequestVar[UserInfoForm](new UserInfoForm(
  WebshopModel.currentUserVar.get match {
    case Some(user) => WebshopDao.findUserById(user.getId getOrElse -1) match {
        case Some(user) => user
        case None => new jpa.party.User
      }
    case None => new jpa.party.User { val party = new jpa.party.Party }
  }))

class UserInfoForm(val user : jpa.party.User) extends Form {
  
  val emailField = TextField(user.getEmail, _ match {
    case Whitespace(s) => error = "No email address specified"; user.setEmail("")
    case EmailAddress(s) => if (emailExists(s)) error = "Email address is already in use"; user.setEmail(s)
    case s => error = "Invalid email address"; user.setEmail(s)
  })

  if (user.getParty == null) {
  	user.setParty(new jpa.party.Party)
  }
  val partyForm = Nested(PartyForm(user.getParty))
  
  def emailExists(email : String) = WebshopDao.findUserByEmail(email) match {
    case Some(u) => user.getId != u.getId
    case None => false
  } 

  def changePasswordLink(href : String) = (xml : NodeSeq) => 
    SHtml.a(() => JsCmds.RedirectTo(ChangePasswordForm.createLink(href, user.getEmail)), xml) % currentAttributes()
  
  def storeButton(label : String) = SHtml.submit(label, () => store) % currentAttributes()
  
  def store = {

    // check validation errors
    if (errors.isEmpty) {

      // store user
      WebshopDao.transaction (_.merge(user))
      S.notice("User info updated")
    }
    else {
      // keep errors
      UserInfoForm.set(this)
    }
  }
}


