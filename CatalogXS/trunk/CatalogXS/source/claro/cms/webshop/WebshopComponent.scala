package claro.cms.webshop

import net.liftweb.http.{RequestVar,Req,S,SHtml,LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import scala.xml.{Node,NodeSeq,Text}
import agilexs.catalogxs.jpa

class WebshopComponent extends Component with WebshopBindingHelpers {
  
  val prefix = "webshop"

  bindings.append {
    case _ : WebshopComponent => Map (
      "id" -> WebshopModel.shop.get.id,
      "current-product-group" -> WebshopModel.currentProductGroup -> "group",
      "current-product" -> WebshopModel.currentProduct -> "product",
      "current-search-string" -> WebshopModel.currentSearchStringVar.is,
      "current-search-products" -> WebshopModel.currentSearchProducts -> "product",
      "products" -> WebshopModel.shop.get.products -> "product",
      "group" -> WebshopModel.shop.get.productGroupsByName.get(@@("name")) -> "group",
      "top-level-groups" -> WebshopModel.shop.get.topLevelProductGroups -> "group",
      "promotions" -> WebshopModel.shop.get.promotions -> "promotion",
      "shopping-cart" -> ShoppingCart -> "shopping-cart",
      "search-all" -> searchAllLink,
      "search-form" -> new SearchForm -> "search",
      "current-user" -> WebshopModel.currentUserVar.get -> "user",
      "login-form" -> LoginForm.is -> "login",
      "logout-link" -> logoutLink,
      "user-info-form" -> UserInfoForm.ExistingUser.get -> "form",
      "register-form" -> UserInfoForm.NewUser.get -> "form",
      "confirm-user" -> UserInfoForm.confirmUser -> "")
    
    case promotion : VolumeDiscountPromotion => Map(         
      "id" -> promotion.id,
      "start-date" -> WebshopUtil.slashDate.format(promotion.startDate),
      "end-date" -> WebshopUtil.slashDate.format(promotion.endDate),
      "price" -> formatMoney(promotion.price, promotion.priceCurrency),
      "volume-discount" -> promotion.volumeDiscount,
      "product" -> promotion.product -> "product")
    
    case product : Product => Map(   
      "id" -> product.id.toString,
      "properties" -> product.properties -> "property",
      "property" -> product.propertiesByName.get(@@("name")) -> "property",
      "value" -> value(product.propertiesByName.get(@@("property"))),
      "groups" -> product.productGroups -> "group",
      "link" -> Link(product),
      "href" -> LinkAttr(product) -> "href")
    
    case group : ProductGroup => Map(   
      "id" -> group.id.toString,
      "name" -> group.name,
      "sub-groups" -> group.children -> "group",
      "parent-groups" -> group.parents -> "group",
      "group-properties" -> group.groupProperties -> "property",
      "group-property" -> group.groupPropertiesByName.get(@@("name")) -> "property",
      "group-value" -> value(group.groupPropertiesByName.get(@@("property"))),
      "properties" -> group.properties -> "property",
      "products" -> @@?("include_sub-groups", group.productExtent, group.products) -> "product",
      "promotions" -> group.productExtentPromotions -> "promotion",
      "link" -> Link(group))
      
    case cart : ShoppingCart => Map(
      "items" -> cart.order.productOrders -> "item",
      "add" -> cart.addProduct(@@("product-prefix", "product")),
      "add-promotion" -> cart.addPromotion(@@("promotion-prefix", "promotion")),
      "total-prices" -> cart.order.totalPrices -> "total-price",
      "clear" -> cart.clear,
      "link" -> Link("/cart"))
    
    case order : Order => Map(
      "items" -> order.order.getProductOrders -> "item",
      "link" -> Link("/order"))

   case productOrder : ProductOrder => Map(   
      "id" -> productOrder.productOrder.getId.toString,
      "product" -> productOrder.product -> "product",
      "price" -> formatMoney(productOrder.price, productOrder.currency),
      "total-price" -> formatMoney(productOrder.totalPrice, productOrder.currency),
      "currency" -> productOrder.currency,
      "volume" -> productOrder.volume.toString,
      "volume-edit" -> ShoppingCart.updateVolume(productOrder),
      "remove" -> ShoppingCart.removeProductOrder(productOrder))

    case login : LoginForm => Map(
      "email-field" -> login.emailField,
      "password-field" -> login.passwordField,
      "login-button" -> login.loginButton,
      "failure" -> login.failure -> "failure")
    
    case failure : LoginFailure => Map(
      "message" -> failure.message
    )
   
    case form : UserInfoForm => Map(
      "errors" -> form.errors -> "error",
      "user" -> form.user -> "user",
      "is-new" -> form.isNew -> "",
      "email-field" -> form.emailField -> "field",
      "password-field" -> form.passwordField -> "field",
      "repeat-password-field" -> form.repeatPasswordField -> "field",
      "name-field" -> form.partyForm.nameField -> "field",
      "phone-field" -> form.partyForm.phoneField -> "field",
      "address-form" -> form.partyForm.addressForm -> "form",
      "party" -> form.partyForm -> "party",
      "store-button" -> form.storeButton(@@("label", "Store"), @@("href", ""), @@("confirm-href", "/confirm")))
        
    case form : PartyForm => Map(
      "name-field" -> form.nameField -> "field",
      "phone-field" -> form.phoneField -> "field",
      "address-form" -> form.addressForm -> "form")
    
    case user : jpa.party.User => Map(
      "email" -> user.getEmail,
      "name" -> user.getParty.getName,
      "address" -> user.getParty.getAddress,
      "is-confirmed" -> user.getConfirmed -> "")
    
    case address : AddressForm => Map(
      "address1-field" -> address.address1Field -> "field",
      "address2-field" -> address.address2Field -> "field",
      "postal-code-field" -> address.postalCodeField -> "field",
      "town-field" -> address.townField -> "field",
      "country-field" -> address.countryField -> "field")
    
    case property: Property => Map(  
      "id" -> property.id.toString,
      "name" -> property.name,
      "label" -> property.name,
      "value" -> value(property))
    
    case money : Money => Map(
      "amount" -> money.amount,
      "currency" -> money.currency,
      "format" -> format(money))
  }
  
  templateLocators.append {
    case Template(name, product : Product) => ResourceLocator(name + "-product", "html",
    	Scope(product.id), 
    	product.productGroupExtent map (g => 
    	  Scope("group" -> g.id)),
        Scope("shop" -> WebshopModel.shop.id),
        Scope("catalog" -> WebshopModel.shop.catalogId),
    	Scope.global)
  }

  rewrite.append {
    case "index" :: Nil => "index" :: Nil
    case "product" :: id :: Nil => WebshopModel.currentProductVar(Some(id)); "product" :: Nil
    case "group" :: id :: Nil => WebshopModel.currentProductGroupVar(Some(id)); "group" :: Nil
    case "group" :: id :: "search" :: s :: Nil => 
      WebshopModel.currentProductGroupVar(Some(id))
      WebshopModel.currentSearchStringVar(Some(s))
      "search" :: Nil
    case "search" :: s :: Nil => WebshopModel.currentSearchStringVar(Some(s)); "search" :: Nil
    case "cart" :: Nil => "shopping_cart" :: Nil
    case path => path
  }
}

