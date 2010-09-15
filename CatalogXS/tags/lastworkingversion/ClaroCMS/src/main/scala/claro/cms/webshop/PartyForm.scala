package claro.cms.webshop

import claro.jpa
import claro.cms.Form

case class PartyForm(party : jpa.party.Party) extends Form {
  if (party.getAddress == null) {
    party.setAddress(new jpa.party.Address)
  }
  if (party.getDeliveryAddress == null) {
    party.setDeliveryAddress(new jpa.party.Address)
  }
  val nameField = TextField(party.getName, party.setName(_))  
  val phoneField = TextField(party.getPhoneNumber, party.setPhoneNumber(_))  
  val addressForm = Nested(new AddressForm(party.getAddress))
  val deliveryAddressForm = Nested(new AddressForm(party.getDeliveryAddress))
}


