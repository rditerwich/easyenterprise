package claro.cms.webshop

import agilexs.catalogxs.jpa

object WebshopDao extends Dao("AgileXS.CatalogXS.Jpa.PersistenceUnit") {
  
  def findUserById(id : Long) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.id = :id", "id" -> id)
  
  def findUserByEmail(email : String) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.email = :email", "email" -> email)
  
  def findUserByEmailAndPassword(email : String, encryptedPassword : String) : Option[jpa.party.User] = 
    querySingle("Select u from User u where u.email = :email and u.password = :password", "email" -> email, "password" -> encryptedPassword)
  
}
