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
  
  def cutters = CatalogDao.getOrCreateCategory("Cutters")

  
  def shop = CatalogDao.getOrCreateShop("Shop")

  def brands = CatalogDao.getOrCreateCategory("Brands")
  def abdick = CatalogDao.getOrCreateCategory("Abdick")
  def agfa = CatalogDao.getOrCreateCategory("Agfa")
  def presstek = CatalogDao.getOrCreateCategory("Presstek")
  def flintgroup = CatalogDao.getOrCreateCategory("The Flint Group")
  def saphira = CatalogDao.getOrCreateCategory("Saphira")
  
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
    
    machines.getChildren.add(cutters)

    brands.getChildren.add(abdick)
    brands.getChildren.add(agfa)
    brands.getChildren.add(flintgroup)
    brands.getChildren.add(presstek)
    brands.getChildren.add(saphira)
    
    val polar66 = CatalogDao.getOrCreateProduct("Polar 66 Quickcutter") useIn { product => 
      CatalogDao.set(product, Properties.articleNumber, "MA18364876")
      CatalogDao.set(product, Properties.description, "Polar snijmachine, model 66", "nl")
      CatalogDao.set(product, Properties.synopsis, "De Polar 66 is een echte 'dienstverlener' in de copy-shop branche, huisdrukkerij en ieder die in de steeds groeiende franchisemarkt een betrouwbare snijmachine zoekt. <p>Meer informatie over de Polar 66? Bel met Tetterode, Sales Support Finishing, tel. 020 44 66 999 of vul het contactformulier in, dan nemen wij contact met u op.", "nl")
      CatalogDao.set(product, Properties.price, 180000.00)
      CatalogDao.setImage(product, Properties.image, getClass, "polar66.png")
      cutters.getChildren.add(product)
    }
    
    adhesiveBinding
    plaat
    inks
    
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
      promotion.setProduct(polar66)
      promotion.setVolumeDiscount(1)
      promotion.setPrice(14990.00)
      promotion.setStartDate(new java.util.Date())
      promotion.setEndDate(new java.util.Date())
      promotion.setShop(shop)
      shop.getPromotions.add(promotion)
    }
  }
  
  def adhesiveBinding = {
    val adhesiveBinding = CatalogDao.getOrCreateCategory("Adhesive Binding")
    postPress.getChildren.add(adhesiveBinding)
  }
  
  def plaat = {
    val plates = CatalogDao.getOrCreateCategory("Plates")
    prePress.getChildren.add(plates)
    supplies.getChildren.add(plates)
    CatalogDao.createProduct("Presstek Anthem Pro", "", "SU18551200", "Presstek has unveiled the Presstek Anthem Pro chemistry-free thermally imaged digital plate. It requires only a water rinse after imaging to prepare it for printing. It does not require gumming, baking, or chemical processing and supports run lengths up to 100,000 impressions. It is compatible with Presstek's Dimension thermal platesetters.", 18.15, getClass, "presstekanthempro.png", presstek, plates)
    CatalogDao.createProduct("NYLOFLEX SPRINT DIGITAL FLEXO PLATE", "", "SU18551341", "Flint Group Flexographic Products has launched a digital version of its water washable flexo printing plate nyloflex Sprint. Designed to meet the high quality requirements of the narrow web and the mid web market, nyloflex Sprint Digital offers high resistance against UV-inks and UV-varnishes and a remarkable performance in printing of finest elements, up to 60 L/cm or even higher. Due to the excellent ink transfer characteristics and low dot gain the nyloflex Sprint Digital shows an outstanding performance in halftone printing.", 22.69, getClass, "nyloflex.png", flintgroup, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 9-3/8 x 13-3/8", "PCG08831", "", 201.15, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 9-1/2 x 13-3/8", "PCG13511", "", 203.83, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15", "PCG05911", "", 240.63, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15-1/2", "PCG08051", "", 248.65, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 15-9/32", "PCG06091", "", 245.14, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Boxed 10 x 16", "PCG07061", "", 256.67, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 9-3/8 x 13-3/8", "PCG08831", "", 1910.15, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 9-1/2 x 13-3/8", "PCG13511", "", 1930.83, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15", "PCG05911", "", 2100.63, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15-1/2", "PCG08051", "", 2180.65, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 15-9/32", "PCG06091", "", 2150.14, getClass, "anthempro.jpg", presstek, plates)
    CatalogDao.createProduct("Anthem Pro", "6Mil Pallet 10 x 16", "PCG07061", "", 2260.67, getClass, "anthempro.jpg", presstek, plates)
    
    CatalogDao.createProduct("AGFA Lithostar Ultra", "90-25-19/32X21-21/32 LAPV+ 8MIL", "PCG07061", "90-25-19/32X21-21/32 LAPV+ 8MIL", 646.65, getClass, "agfalithostar.jpg", agfa, plates)
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "20L-AGFA L5000B ULTRA DEVELOPER", "LXAZW000", "20L-AGFA L5000B ULTRA DEVELOPER", 131.55, getClass, "agfalithostar.jpg", agfa, plates)
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "20L-AGFA L5000B ULTRA DEVELOPER", "OETTT000", "20L-AGFA L5000B ULTRA DEVELOPER", 120.20, getClass, "agfalithostar.jpg", agfa, plates)
    CatalogDao.createProduct("AGFA Lithostar Chemistry", "3-LITHOSTAR LAPV CORR PEN BROAD TIP", "P9EHY000", "3-LITHOSTAR LAPV CORR PEN BROAD TIP", 45.05, getClass, "agfalithostar.jpg", agfa, plates)
  }
  
  def inks = {
	  val ink = CatalogDao.getOrCreateCategory("Ink")
	  press.getChildren.add(ink)
	  supplies.getChildren.add(ink)
	  val blackInk = CatalogDao.getOrCreateCategory("Black Ink")
	  val colorInk = CatalogDao.getOrCreateCategory("Color Ink")
	  val processInkSet = CatalogDao.getOrCreateCategory("Process Ink Sets")
	  val magneticInks = CatalogDao.getOrCreateCategory("Magnetic Inks")
	  ink.getChildren.add(blackInk)
	  ink.getChildren.add(colorInk)
	  ink.getChildren.add(processInkSet)
	  ink.getChildren.add(magneticInks)
	  CatalogDao.createProduct("Abdick Multigraphics RB900 black", "Abdick RB900 black rubber base CAN", "83-9-104411", "", 9.97, getClass, "abdick.jpg", abdick, blackInk)
	  CatalogDao.createProduct("Abdick Multigraphics RB900 black", "Abdick RB900 black rubber base 5LB CAN", "83-9-104411", "", 48.11, getClass, "abdick.jpg", abdick, blackInk)
	  CatalogDao.createProduct("Abdick Multigraphics RB900 black", "Abdick RB900 black rubber base Cartiridge", "83-9-104411", "", 9.39, getClass, "abdick.jpg", abdick, blackInk)
	 
	  val saphiraInks = CatalogDao.getOrCreateCategory("Saphira Inks")
	  saphira.getChildren.add(saphiraInks)
	  ink.getChildren.add(saphiraInks)
	  val saphiraBioMag = CatalogDao.createProduct("Saphira Ink Bio-speed Magenta", "", "SU18364577", "Magenta bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioMag.jpg", saphiraInks, colorInk)
	  CatalogDao.createProduct("Saphira Ink Bio-speed Yellow", "SU18364578", "", "Yellow bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioYellow.jpg", saphiraInks, colorInk)
	  CatalogDao.createProduct("Saphira Ink Bio-speed Blue", "SU18364579", "", "Blue bio process ink for best results at a high speed", 8.55, getClass, "saphiraBioBlue.jpg", saphiraInks, colorInk)
	
	  new VolumeDiscountPromotion useIn { promotion =>
	  promotion.setProduct(saphiraBioMag)
	  promotion.setVolumeDiscount(3)
	  promotion.setPrice(7.55)
	  promotion.setStartDate(new java.util.Date())
	  promotion.setEndDate(new java.util.Date())
	  promotion.setShop(shop)
	  shop.getPromotions.add(promotion)
    }

  }
}