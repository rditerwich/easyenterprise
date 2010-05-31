package claro.cms.webshop

import claro.jpa
import claro.cms.Form

case class PartyForm(party : jpa.party.Party) extends Form {
  val nameField = TextField(party.getName, party.setName(_))  
  val phoneField = TextField(party.getPhoneNumber, party.setPhoneNumber(_))  
  val addressForm = Nested(new AddressForm(party.getAddress))
}


