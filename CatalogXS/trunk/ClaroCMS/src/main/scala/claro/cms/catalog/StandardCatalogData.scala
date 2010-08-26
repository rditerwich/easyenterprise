package claro.cms.catalog

import claro.cms.{Cms,Dao}
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

  def saphiraInks = CatalogDao.getOrCreateCategory("Saphira Inks")
  
  def shop = CatalogDao.getOrCreateShop("Shop")
  
  def createNavigation(category : Category, index : Int) = 
    new Navigation useIn { nav =>
      nav.setCategory(category)
      nav.setIndex(index)
    }
  
  def createSampleData() = {
    val root = CatalogDao.catalog.getRoot
    
    root.getChildren.clear
    root.getChildren.add(supplies)
    root.getChildren.add(spareParts)
    root.getChildren.add(services)
    
    press.getChildren.add(ink)
    ink.getChildren.add(saphiraInks)
    CatalogDao.getOrCreateProduct("Saphira Ink Bio-speed Magenta") useIn { product =>
      CatalogDao.set(product, Properties.articleNumber, "SU18364577")
      CatalogDao.set(product, Properties.description, "Magenta bio process ink for best results at a high speed")
      CatalogDao.set(product, Properties.price, 8.55)
    }
    
    shop useIn { nav =>
      nav.getNavigation.clear
      nav.getNavigation.add(createNavigation(null, 0) useIn { nav =>
        nav.getSubNavigation.add(createNavigation(machines, 0))
        nav.getSubNavigation.add(createNavigation(brands, 1))
      })
      nav.getNavigation.add(createNavigation(null, 1) useIn { nav =>
        nav.getSubNavigation.add(createNavigation(supplies, 0))
        nav.getSubNavigation.add(createNavigation(spareParts, 1))
        nav.getSubNavigation.add(createNavigation(services, 2))
      })
      nav.getNavigation.add(createNavigation(null, 2) useIn { nav =>
        nav.getSubNavigation.add(createNavigation(prePress, 0))
        nav.getSubNavigation.add(createNavigation(press, 1))
        nav.getSubNavigation.add(createNavigation(postPress, 2))
      })
    }
  }
}