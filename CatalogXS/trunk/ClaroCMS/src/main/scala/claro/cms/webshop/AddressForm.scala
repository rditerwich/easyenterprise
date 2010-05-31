package claro.cms.webshop

import net.liftweb.http.{S,SHtml,SessionVar}
import xml.{Node, NodeSeq, MetaData}
import claro.common.util.Conversions._
import claro.jpa
import claro.cms.Form

class AddressForm(address : jpa.party.Address) extends Form {

  val address1Field = TextField(address.getAddress1, address.setAddress1(_))  
  val address2Field = TextField(address.getAddress2, address.setAddress2(_))  
  val postalCodeField = TextField(address.getPostalCode, address.setPostalCode(_))  
  val townField = TextField(address.getTown, address.setTown(_))  
  val countryField = TextField(address.getCountry, address.setCountry(_))  
}
