package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import java.security.MessageDigest
import java.nio.charset.Charset
import claro.common.util.Conversions._
import claro.jpa
import claro.cms.{Form}

object ChangePasswordForm extends RequestVar[ChangePasswordForm](new ChangePasswordForm) {
  
  def createLink(href : String, email : String) = {
    
    val now = System.currentTimeMillis
    val key = encrypt(email + now + Math.random)
    val Hours = 60 * 60 * 1000
    
    val confirmation = WebshopDao.findEmailConfirmationByEmail(email) match {
      case Some(conf) => conf
      case None => new jpa.party.EmailConfirmation
    }
    confirmation.setEmail(email)
    confirmation.setConfirmationKey(key)
    confirmation.setExpirationTime(now + 24 * Hours)
    WebshopDao.transaction(_.merge(confirmation))
    
    val req = S.request.open_!.request
    req.scheme + "://" + req.serverName + ":" + req.serverPort + req.contextPath + href + "?email=" + email + "&key=" + key
  }
  
  def encrypt(password : String) : String = {
    val md = MessageDigest.getInstance("SHA-1")
    val salt = "SALT&PEPPER"
    val charset = "UTF-8"
    md.update(salt.getBytes(charset))
    md.update(password.getBytes(charset))
    val bytes = md.digest.map(_.asInstanceOf[Int] & 0xff)
    val codes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuwvxyz01234567890+-"
    val chars = bytes.map(b => codes(b & 0x0f)) ++ bytes.map(b => codes(b >> 4))
    new String(chars.toArray)
  }

}

class ChangePasswordForm extends Form {
  val email = S.request.open_!.param("email").getOrElse("")
  val confirmationKey = S.request.open_!.param("key").getOrElse("")
  var password = ""
  var repeatPassword = ""

  def isValid : Boolean = WebshopDao.findEmailConfirmationByEmail(email) match {
    case Some(conf) => isValid(conf)
    case None => false
  }
  
  def isValid(conf : jpa.party.EmailConfirmation) : Boolean = {
  	println(conf.getConfirmationKey)
    conf.getConfirmationKey == confirmationKey && conf.getExpirationTime.getOrElse(0) > System.currentTimeMillis
  }
  
  val passwordField = PasswordField(password, _ match {
    case Whitespace(s) => error = "Password must not be empty"; password = ""
    case s => password = s
  })
  
  val repeatPasswordField = PasswordField(repeatPassword, _ match {
    case s if (password != s) => error = "Passwords do not match"; repeatPassword = ""
    case s => repeatPassword = s
  })

  def changePasswordButton(label : String, href : String) = SHtml.submit(label, () => changePassword(href)) % currentAttributes()

  def changePassword(href : String) = {
    
    ChangePasswordForm.set(this)
    
    // store user when there are no validation errors
    if (errors.isEmpty) {
      var success = false

      WebshopDao.transaction { em =>
        WebshopDao.findEmailConfirmationByEmail(email) match {
          case Some(conf) =>
  
            // confirmation key correct?
            if (isValid(conf)) {

              // find user
              WebshopDao.findUserByEmail(email) match {
                case Some(user) =>
    
                  // store password
                  user.setPassword(ChangePasswordForm.encrypt(password))

                  // remove confirmation
                  em.remove(conf)

                  // log in
                  WebshopModel.currentUserVar.set(Some(user))
                  success = true
                case None => error = "Couldn't change password"
              }
            }
          case None => error = "Couldn't change password"
        }
      }
      
      if (success) 
      	S.redirectTo(href)
    }
  }
}
