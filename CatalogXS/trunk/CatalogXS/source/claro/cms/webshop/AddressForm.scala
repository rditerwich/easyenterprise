package claro.cms.webshop

import net.liftweb.http.{S,SHtml,SessionVar}
import xml.{Node, NodeSeq, MetaData}
import agilexs.catalogxs.jpa
import claro.common.util.Conversions._

class AddressForm(address : jpa.party.Address) extends BindingHelpers {

  def address1Field = SHtml.text(address.getAddress1.getOrElse(""), address.setAddress1(_)) % currentAttributes() 
  def address2Field = SHtml.text(address.getAddress2.getOrElse(""), address.setAddress2(_)) % currentAttributes() 
  def postalCodeField = SHtml.text(address.getPostalCode.getOrElse(""), address.setPostalCode(_)) % currentAttributes() 
  def townField = SHtml.text(address.getTown.getOrElse(""), address.setTown(_)) % currentAttributes() 
  def countryField = SHtml.text(address.getCountry.getOrElse(""), address.setCountry(_)) % currentAttributes() 
}
