package claro.cms.webshop

import claro.jpa.catalog._
import claro.jpa.party._
import claro._
import claro.cms.Dao

object WebshopDao extends Dao {

  val dataSource = "claro.jpa.PersistenceUnit"

  def findCatalog(id : Long) : Option[Catalog] = 
    querySingle("Select from Catalog c where c.id = :id", "id" -> id)
  
  def findUserById(id : Long) : Option[User] = 
    querySingle("Select u from User u where u.id = :id", "id" -> id)
  
  def findUserByEmail(email : String) : Option[User] = 
    querySingle("Select u from User u where u.email = :email", "email" -> email)
  
  def findUserByEmailAndPassword(email : String, encryptedPassword : String) : Option[User] = 
    querySingle("Select u from User u where u.email = :email and u.password = :password", "email" -> email, "password" -> encryptedPassword)
  
  def findEmailConfirmationByEmail(email : String) : Option[EmailConfirmation] = 
    querySingle("Select c from EmailConfirmation c where c.email = :email", "email" -> email)
}
