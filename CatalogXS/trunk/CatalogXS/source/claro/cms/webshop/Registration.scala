package claro.cms.webshop

import net.liftweb.http.{S,SHtml,SessionVar}
import xml.{Node, NodeSeq, MetaData}
import agilexs.catalogxs.jpa
import claro.common.util.Conversions._

object RegistrationForm extends SessionVar[RegistrationForm](new RegistrationForm)

class RegistrationForm extends BindableForm {

  var user : jpa.party.User = null
  var repeatPassword = ""
  def customer = user.getParty
  
  reset
  
  def emailField = SHtml.text(user.getEmail.getOrElse(""), user.setEmail(_)) % currentAttributes() 
  def passwordField = SHtml.password(user.getPassword.getOrElse(""), user.setPassword(_)) % currentAttributes() 
  def repeatPasswordField = SHtml.password(repeatPassword, repeatPassword = _) % currentAttributes()
  def nameField = SHtml.text(customer.getName.getOrElse(""), customer.setName(_)) % currentAttributes() 
  def phoneField = SHtml.text(customer.getPhoneNumber.getOrElse(""), customer.setPhoneNumber(_)) % currentAttributes() 
  def addressForm = new AddressForm(customer.getAddress)
  def registerButton = SHtml.submit("Register", () => register) % currentAttributes()
  
  def validatePassword = if (user.getPassword != repeatPassword) Some("Passwords do not match") else None
  
  def validateEmail = {
    val email = user.getEmail.getOrElse("")
    if (email.trim == "") {
      Some("No email address specified")
    } 
    else if (email.indexOf('@') < 1 || email.indexOf('@') != email.lastIndexOf('@') || !email.afterFirst('@').contains('.')) {
      Some("Email address invalid")
    }
    else if (emailExists) {
      Some("Email address is already being used")
    }
    else None
  }
  
  def validate = validateEmail ++ validatePassword
  
  def emailExists = WebshopModel.dbaccess { em =>
    val query = em.createQuery("Select u from User u where u.email = :email")
    query.setParameter("email", user.getEmail)
    !query.getResultList.isEmpty
  }
  
  def reset = {
    val customer = new jpa.party.Party
    val address = new jpa.party.Address
    user = new jpa.party.User
    user.setParty(customer)
    customer.setAddress(address)
    repeatPassword = ""
  }
  
  def register = {
    val errors = validate
    for (error <- errors) S.error(error)

    // store user when there are no validation errors
    if (errors isEmpty) {
      WebshopModel.dbaccess (_.persist(user))
      WebshopModel.currentUserVar.set(Some(user))
      reset
      S.redirectTo("/registered")
    }
  }
}


