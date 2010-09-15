package claro.cms.webshop

import net.liftweb.http.{S,SHtml,RequestVar}
import net.liftweb.http.js.JsCmds
import net.liftweb.util.Mailer.{To,Subject,PlainMailBodyType}
import xml.{Node, NodeSeq, MetaData}
import claro.jpa
import claro.cms.{Form,Mail,Website}

object ShippingOptionsForm extends RequestVar[ShippingOptionsForm](new ShippingOptionsForm) 

class SelectedShippingOption(var checked : Boolean, val option : ShippingOption) {
  def field = SHtml.checkbox(checked, x => checked = x, ("type", "radio"), "name" -> "g1")
}

class ShippingOptionsForm extends Form {

  def order = WebshopModel.currentOrder.get

  def shippingOptions = List(
    new SelectedShippingOption(false, ShippingOption("Normal delivery", Money(1500, "EUR"))),
    new SelectedShippingOption(false, ShippingOption("Express delivery", Money(4500, "EUR")))
  )
  
  var shippingOption : Option[ShippingOption] = None
  
  def proceedOrderLink = (xml : NodeSeq) => 
    <a href="/order_confirmation">{xml}</a> % currentAttributes()

  if (order.delegate.getDeliveryAddress == null) {
    val address = new jpa.party.Address
    
    // copy delivery address from user
    WebshopModel.currentUserVar.get match {
      case Some(user) if (user.getParty.getDeliveryAddress != null) =>
        address.setAddress1(user.getParty.getDeliveryAddress.getAddress1)
        address.setAddress2(user.getParty.getDeliveryAddress.getAddress2)
        address.setTown(user.getParty.getDeliveryAddress.getTown)
        address.setPostalCode(user.getParty.getDeliveryAddress.getPostalCode)
        address.setCountry(user.getParty.getDeliveryAddress.getCountry)
      case _ =>
    }
    order.delegate.setDeliveryAddress(address)
  }
    
  val deliveryAddressForm = Nested(new AddressForm(order.delegate.getDeliveryAddress))
}