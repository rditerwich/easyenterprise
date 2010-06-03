package claro.cms.webshop

import claro.jpa
import claro.cms.Dao

object WebshopDao extends Dao("claro.jpa.PersistenceUnit") {
  
  def findUserById(id : Long) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.id = :id", "id" -> id)
  
  def findUserByEmail(email : String) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.email = :email", "email" -> email)
  
  def findUserByEmailAndPassword(email : String, encryptedPassword : String) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.email = :email and u.password = :password", "email" -> email, "password" -> encryptedPassword)
  
  def findEmailConfirmationByEmail(email : String) : Option[jpa.party.EmailConfirmation] = 
    querySingle("Select c from EmailConfirmation c where c.email = :email", "email" -> email)
}
