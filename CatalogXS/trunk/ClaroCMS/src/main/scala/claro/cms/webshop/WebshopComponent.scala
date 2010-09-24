package claro.cms.webshop

import net.liftweb.http.{RequestVar,Req,S,SHtml,LiftRules,RewriteRequest,RewriteResponse,ParsePath,InMemoryResponse,NotFoundResponse}
import claro.cms.{Cms,Component,Template,ResourceLocator,Scope,Paging}
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
      "current-products" -> WebshopModel.currentProducts -> "product",
      "current-search-string" -> WebshopModel.currentSearchStringVar.is,
      "current-search-products" -> WebshopModel.currentSearchProducts -> "product",
      "products" -> WebshopModel.shop.get.products -> "product",
      "catagory" -> WebshopModel.shop.get.categoriesByName.get(@@("name")) -> "category",
      "top-level-categories" -> grouped(WebshopModel.shop.topLevelCategories) -> "category",
      "trail" -> Trail -> "trail",
      "first-on-trail" -> Trail.firstOnTrail -> "category",
      "filtering" -> Filtering.get, 
      "promotions" -> WebshopModel.shop.get.promotions -> "promotion",
      "shopping-cart" -> ShoppingCart.is,
      "search-all-link" -> searchAllLink,
      "search-is-filtered-on" -> Filtering.hasSearchFilter(WebshopModel.currentSearchStringVar.is.getOrElse("")),
      "add-search-filter-link" -> Filtering.addSearchFilterLink(WebshopModel.currentSearchStringVar.is.getOrElse("")),
      "search-form" -> new SearchForm,
      "current-user" -> WebshopModel.currentUserVar.get -> "user",
      "login-form" -> LoginForm.is,
      "logout-link" -> logoutLink,
      "user-info-form" -> UserInfoForm,
      "register-form" -> RegistrationForm,
      "shipping-options-form" -> ShippingOptionsForm,
      "change-password-form" -> ChangePasswordForm)
    
    case filter : Filter => Map(
    		"title" -> filter.title,
    		"remove-link" -> filter.removeLink
		)
		
    case promotion : VolumeDiscountPromotion => Map(         
      "id" -> promotion.id,
      "start-date" -> WebshopUtil.slashDate.format(promotion.startDate),
      "end-date" -> WebshopUtil.slashDate.format(promotion.endDate),
      "price" -> formatMoney(promotion.price, promotion.priceCurrency),
      "volume-discount" -> promotion.volumeDiscount.asInstanceOf[Long],
      "product" -> promotion.product -> "product")
    
    case product : Product => Map(   
      "id" -> product.id,
      "properties" -> product.properties -> "property",
      "property" -> product.property(locale, @@("name")) -> "property",
      "value" -> value(product.property(locale, @@("property"))),
      "categories" -> product.categories -> "category",
      "is-on-trail" -> Trail.isOnTrail(product),
      "is-selected" -> Trail.isSelected(product),
      "add-to-cart-link" -> ShoppingCart.addProduct(product),
      "link" -> Link(product),
      "href" -> LinkAttr(product) -> "href")
    
    case category : Category => Map(   
      "id" -> category.id,
      "name" -> category.name,
      "sub-categories" -> category.children -> "category",
      "parent-categories" -> category.parents -> "category",
      "properties" -> category.groupProperties(locale) -> "property",
      "property" -> category.groupProperty(locale, @@("name")) -> "property",
      "value" -> value(category.groupProperty(locale, @@("property"))),
      "defined-properties" -> category.properties -> "property",
      "products" -> @@?("include-sub-categories", category.productExtent, category.products) -> "product",
      "promotions" -> category.productExtentPromotions -> "promotion",
      "is-on-trail" -> Trail.isOnTrail(category),
      "is-selected" -> Trail.isSelected(category),
      "link" -> Link(category),
      "is-filtered-on" -> Filtering.hasCategoryFilter(category),
      "add-filter-link" -> Filtering.addFilterLink(category))
      
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
      "id" -> productOrder.productOrder.getId.getOrElse(-1),
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
      "address-form" -> form.partyForm.addressForm,
      "party" -> form.partyForm,
      "register-button" -> form.registerButton(@@("label", "Register"), @@("href", "/registered"), @@("change-password-href", "/changepassword")))
    
    case form : UserInfoForm => Map(
      "errors" -> form.errors -> "error",
      "user" -> form.user -> "user",
      "email-field" -> form.emailField -> "field",
      "name-field" -> form.partyForm.nameField -> "field",
      "phone-field" -> form.partyForm.phoneField -> "field",
      "address-form" -> form.partyForm.addressForm,
      "delivery-address-form" -> form.partyForm.deliveryAddressForm,
      "party" -> form.partyForm,
      "change-password-link" -> form.changePasswordLink(@@("change-password-href", "/changepassword")),
      "store-button" -> form.storeButton(@@("label", "Store")))
      
    case form : ShippingOptionsForm => Map(
      "errors" -> form.errors -> "error",
      "shipping-options" -> form.shippingOptions -> "shipping-option",
      "delivery-address-form" -> form.deliveryAddressForm,
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
      "is-valid" -> form.isValid,
      "change-password-button" -> form.changePasswordButton(@@("label", "Change password"), @@("href", "/passwordchanged")))
        
    case form : PartyForm => Map(
      "name-field" -> form.nameField -> "field",
      "phone-field" -> form.phoneField -> "field",
      "address-form" -> form.addressForm)
    
    case user : jpa.party.User => Map(
      "email" -> user.getEmail,
      "name" -> user.getParty.getName,
      "address" -> user.getParty.getAddress -> "address",
      "has-password" -> (user.getPassword.getOrElse("") != ""))
    
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
      "category" :: Nil
    case "search" :: s :: Nil => WebshopModel.currentSearchStringVar(Some(s)); "search" :: Nil
    case "cart" :: Nil => "shopping_cart" :: Nil
    case "flushcache" :: Nil => 
      WebshopModel.flush 
      "index" :: Nil
    case path => path
  }
}

