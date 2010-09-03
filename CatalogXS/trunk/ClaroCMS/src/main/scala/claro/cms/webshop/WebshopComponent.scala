package claro.cms.webshop

import net.liftweb.http.{RequestVar,Req,S,SHtml,LiftRules,RewriteRequest,RewriteResponse,ParsePath,InMemoryResponse,NotFoundResponse}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import scala.xml.{Node,NodeSeq,Text}
import claro.jpa
import claro.common.util.Conversions._

class WebshopComponent extends Component with WebshopBindingHelpers {
  
  val prefix = "webshop"
    
  bindings.append {
    case _ : WebshopComponent => Map (
      "id" -> WebshopModel.shop.get.id,
      "current-category" -> WebshopModel.currentCategory -> "category",
      "current-product" -> WebshopModel.currentProduct -> "product",
      "current-search-string" -> WebshopModel.currentSearchStringVar.is,
      "current-search-products" -> WebshopModel.currentSearchProducts -> "product",
      "products" -> WebshopModel.shop.get.products -> "product",
      "catagory" -> WebshopModel.shop.get.categoriesByName.get(@@("name")) -> "category",
      "navigation" -> grouped(WebshopModel.shop.navigation) -> "category",
      "filters" -> Filter.filters -> "filter",
      "promotions" -> WebshopModel.shop.get.promotions -> "promotion",
      "shopping-cart" -> ShoppingCart -> "shopping-cart",
      "search-all" -> searchAllLink,
      "search-form" -> new SearchForm -> "search",
      "current-user" -> WebshopModel.currentUserVar.get -> "user",
      "login-form" -> LoginForm.is -> "login",
      "logout-link" -> logoutLink,
      "user-info-form" -> UserInfoForm.get -> "form",
      "register-form" -> RegistrationForm.get -> "form",
      "shipping-options-form" -> ShippingOptionsForm.get -> "form",
      "change-password-form" -> ChangePasswordForm.get -> "form")
    
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
      "property" -> product.property(locale, @@("name")) -> "property",
      "value" -> value(product.property(locale, @@("property"))),
      "categories" -> product.categories -> "category",
      "link" -> Link(product),
      "href" -> LinkAttr(product) -> "href")
    
    case category : Category => Map(   
      "id" -> category.id.toString,
      "name" -> category.name,
      "sub-categories" -> category.children -> "category",
      "parent-categories" -> category.parents -> "category",
      "category-properties" -> category.groupProperties(locale) -> "property",
      "category-property" -> category.groupProperty(locale, @@("name")) -> "property",
      "category-value" -> value(category.groupProperty(locale, @@("property"))),
      "properties" -> category.properties -> "property",
      "products" -> @@?("include-sub-groups", category.productExtent, category.products) -> "product",
      "promotions" -> category.productExtentPromotions -> "promotion",
      "link" -> Link(category))
      
    case cart : ShoppingCart => Map(
      "items" -> cart.order.productOrders -> "item",
      "add" -> cart.addProduct(@@("product-prefix", "product")),
      "add-promotion" -> cart.addPromotion(@@("promotion-prefix", "promotion")),
      "shipping-costs" -> format(cart.order.shippingCosts),
      "total-prices" -> cart.order.totalPrices -> "total-price",
      "total-prices-plus-shipping" -> cart.order.totalPricesPlusShipping -> "total-price",
      "clear" -> cart.clear,
      "link" -> Link("/cart"),
      "place-order" -> cart.placeOrderLink,
      "proceed-order-link" -> cart.proceedOrderLink)
    
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

   case filter : Filter => Map(
       "title" -> filter.title,
       "values" -> filter.values -> "value")
      
   case value : FilterValue => Map(
       "value" -> value.value) 
       
    case login : LoginForm => Map(
      "email-field" -> login.emailField,
      "password-field" -> login.passwordField,
      "login-button" -> login.loginButton,
      "failure" -> login.failure -> "failure",
      "forgot-password-link" -> login.forgotPasswordLink(@@("href", "/passwordreset"), @@("change-password-href", "/changepassword")))
    
    case failure : LoginFailure => Map(
      "message" -> failure.message
    )
   
    case form : RegistrationForm => Map(
      "errors" -> form.errors -> "error",
      "user" -> form.user -> "user",
      "email-field" -> form.emailField -> "field",
      "name-field" -> form.partyForm.nameField -> "field",
      "phone-field" -> form.partyForm.phoneField -> "field",
      "address-form" -> form.partyForm.addressForm -> "form",
      "party" -> form.partyForm -> "party",
      "register-button" -> form.registerButton(@@("label", "Register"), @@("href", "/registered"), @@("change-password-href", "/changepassword")))
    
    case form : UserInfoForm => Map(
      "errors" -> form.errors -> "error",
      "user" -> form.user -> "user",
      "email-field" -> form.emailField -> "field",
      "name-field" -> form.partyForm.nameField -> "field",
      "phone-field" -> form.partyForm.phoneField -> "field",
      "address-form" -> form.partyForm.addressForm -> "form",
      "delivery-address-form" -> form.partyForm.deliveryAddressForm -> "form",
      "party" -> form.partyForm -> "party",
      "change-password-link" -> form.changePasswordLink(@@("change-password-href", "/changepassword")),
      "store-button" -> form.storeButton(@@("label", "Store")))
      
    case form : ShippingOptionsForm => Map(
      "errors" -> form.errors -> "error",
      "shipping-options" -> form.shippingOptions -> "shipping-option",
      "delivery-address-form" -> form.deliveryAddressForm -> "form",
      "proceed-order-link" -> form.proceedOrderLink)
      
    case shippingOption : SelectedShippingOption => Map(
      "description" -> shippingOption.option.description,
      "field" -> shippingOption.field,
      "price" -> format(shippingOption.option.price))
        
    case form : ChangePasswordForm => Map(
      "errors" -> form.errors -> "error",
      "email" -> form.email,
      "password-field" -> form.passwordField -> "field",
      "repeat-password-field" -> form.repeatPasswordField -> "field",
      "is-valid" -> form.isValid -> "",
      "change-password-button" -> form.changePasswordButton(@@("label", "Change password"), @@("href", "/passwordchanged")))
        
    case form : PartyForm => Map(
      "name-field" -> form.nameField -> "field",
      "phone-field" -> form.phoneField -> "field",
      "address-form" -> form.addressForm -> "form")
    
    case user : jpa.party.User => Map(
      "email" -> user.getEmail,
      "name" -> user.getParty.getName,
      "address" -> user.getParty.getAddress,
      "has-password" -> (user.getPassword.getOrElse("") != "") -> "")
    
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
    case Template(name, product : Product) => ResourceLocator(List(name + "-product"), "html",
    	Scope(product.id), 
    	product.categoryExtent map (g => 
    	  Scope("group" -> g.id)),
        Scope("shop" -> WebshopModel.shop.id),
        Scope("catalog" -> WebshopModel.shop.catalogId),
    	Scope.global)
  }

  dispatch.append {
    case ("catalog" :: "media" :: id :: Nil, suffix) => 
      WebshopModel.shop.mediaValues.get(id.toLongOr(0)) match {
      case Some((mimeType, image)) => 
        val headers = List(
          ("Cache-Control", "public, max-age=3600"), 
          ("Pragma", "public"), 
          ("Content-Length", image.length.toString),
          ("Content-Type", mimeType)) 
        InMemoryResponse(image, headers, Nil, 200)
      case _ =>  NotFoundResponse()
    }
  }
  
  rewrite.append {
    case "index" :: Nil => "index" :: Nil
    case "product" :: id :: Nil => WebshopModel.currentProductVar(Some(id)); "product" :: Nil
    case "category" :: urlName :: Nil => WebshopModel.currentCategoryVar(Some(urlName)); "category" :: Nil
    case "category" :: urlName :: "search" :: s :: Nil => 
      WebshopModel.currentCategoryVar(Some(urlName))
      WebshopModel.currentSearchStringVar(Some(s))
      "search" :: Nil
    case "search" :: s :: Nil => WebshopModel.currentSearchStringVar(Some(s)); "search" :: Nil
    case "cart" :: Nil => "shopping_cart" :: Nil
    case "flushcache" :: Nil => 
      WebshopModel.flush 
      "index" :: Nil
    case path => path
  }
}

