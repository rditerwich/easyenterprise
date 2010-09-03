package claro.cms.catalog.data

import claro.cms.{Cms,Dao}
import claro.cms.catalog.{CatalogDao}
import claro.jpa.catalog._
import claro.jpa.shop._
import claro.common.util.Conversions._
import scala.collection.JavaConversions._

object StandardCatalogData extends Dao {

  val dataSource = CatalogDao.dataSource
  val Properties = CatalogDao.Properties
  
  def supplies = CatalogDao.getOrCreateCategory("Supplies")
  def spareParts = CatalogDao.getOrCreateCategory("Spare Parts")
  def services = CatalogDao.getOrCreateCategory("Services")
  
  def prePress = CatalogDao.getOrCreateCategory("Pre Press")
  def press = CatalogDao.getOrCreateCategory("Press")
  def postPress = CatalogDao.getOrCreateCategory("Post Press")
  
  def machines = CatalogDao.getOrCreateCategory("Machines")
  def brands = CatalogDao.getOrCreateCategory("Brands")
  
  def ink = CatalogDao.getOrCreateCategory("Ink")
  def cutters = CatalogDao.getOrCreateCategory("Cutters")

  def saphiraInks = CatalogDao.getOrCreateCategory("Saphira Inks")
  
  def shop = CatalogDao.getOrCreateShop("Shop")
  
  def createNavigation(shop : Shop, parent : Navigation, category : Category, index : Int) = 
    new Navigation useIn { nav =>
      nav.setCategory(category)
      nav.setIndex(index)
      if (shop != null) {
        nav.setParentShop(shop)
        shop.getNavigation.add(nav)
      }
      if (parent != null) {
        nav.setParentNavigation(parent)
        parent.getSubNavigation.add(nav)
      }
    }
  
  def createSampleData() = transaction { em =>
    CatalogDao.catalog.setName("Catalog")
    val root = CatalogDao.catalog.getRoot
    
    root.getChildren.clear
    root.getChildren.add(supplies)
    root.getChildren.add(spareParts)
    root.getChildren.add(services)
    
    press.getChildren.add(ink)
    ink.getChildren.add(saphiraInks)
    machines.getChildren.add(cutters)
    
    val saphiraBioMag = CatalogDao.createProduct("Saphira Ink Bio-speed Magenta", "SU18364577", "Magenta bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioMag.jpg", ink)
    CatalogDao.createProduct("Saphira Ink Bio-speed Yellow", "SU18364578", "Yellow bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioYellow.jpg", ink)
    CatalogDao.createProduct("Saphira Ink Bio-speed Blue", "SU18364579", "Blue bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioBlue.jpg", ink)
    CatalogDao.createProduct("Saphira Ink Bio-speed Blue", "SU18364579", "Blue bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioBlue.jpg", ink)
    
    val polar66 = CatalogDao.getOrCreateProduct("Polar 66 Quickcutter") useIn { product => 
      CatalogDao.set(product, Properties.articleNumber, "MA18364876")
      CatalogDao.set(product, Properties.description, "Polar snijmachine, model 66", "nl")
      CatalogDao.set(product, Properties.synopsis, "De Polar 66 is een echte 'dienstverlener' in de copy-shop branche, huisdrukkerij en ieder die in de steeds groeiende franchisemarkt een betrouwbare snijmachine zoekt. <p>Meer informatie over de Polar 66? Bel met Tetterode, Sales Support Finishing, tel. 020 44 66 999 of vul het contactformulier in, dan nemen wij contact met u op.", "nl")
      CatalogDao.set(product, Properties.price, 180000.00)
      CatalogDao.setImage(product, Properties.image, getClass, "polar66.png")
      cutters.getChildren.add(product)
    }
    
    shop.getNavigation.clear()
    createNavigation(shop, null, null, 0) useIn { nav =>
      createNavigation(null, nav, machines, 0)
      createNavigation(null, nav, brands, 1)
    }
    createNavigation(shop, null, null, 1) useIn { nav =>
      createNavigation(null, nav, supplies, 0)
      createNavigation(null, nav, spareParts, 1)
      createNavigation(null, nav, services, 2)
    }
    createNavigation(shop, null, null, 2) useIn { nav =>
      createNavigation(null, nav, prePress, 0)
      createNavigation(null, nav, press, 1)
      createNavigation(null, nav, postPress, 2)
    }
    
    new VolumeDiscountPromotion useIn { promotion =>
      promotion.setProduct(saphiraBioMag)
      promotion.setVolumeDiscount(3)
      promotion.setPrice(7.55)
      promotion.setStartDate(new java.util.Date())
      promotion.setEndDate(new java.util.Date())
      promotion.setShop(shop)
      shop.getPromotions.add(promotion)
    }
    new VolumeDiscountPromotion useIn { promotion =>
      promotion.setProduct(polar66)
      promotion.setVolumeDiscount(1)
      promotion.setPrice(14990.00)
      promotion.setStartDate(new java.util.Date())
      promotion.setEndDate(new java.util.Date())
      promotion.setShop(shop)
      shop.getPromotions.add(promotion)
    }
  }
}