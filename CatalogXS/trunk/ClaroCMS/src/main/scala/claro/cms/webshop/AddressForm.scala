package claro.cms.webshop

import net.liftweb.http.{S,SHtml,SessionVar}
import xml.{Node, NodeSeq, MetaData}
import claro.common.util.Conversions._
import claro.jpa
import claro.cms.Form

class AddressForm(address : jpa.party.Address) extends Form {

  if (address.getAddress1 == null) address.setAddress1("")
  if (address.getAddress2 == null) address.setAddress2("")
  if (address.getTown == null) address.setTown("")
  if (address.getCountry == null) address.setCountry("")
  if (address.getPostalCode == null) address.setPostalCode("")
  
  val address1Field = TextField(address.getAddress1, address.setAddress1(_))  
  val address2Field = TextField(address.getAddress2, address.setAddress2(_))  
  val postalCodeField = TextField(address.getPostalCode, address.setPostalCode(_))  
  val townField = TextField(address.getTown, address.setTown(_))  
  val countryField = TextField(address.getCountry, address.setCountry(_))  
}
