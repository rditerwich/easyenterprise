package claro.cms.catalog

import net.liftweb.util.ThreadGlobal
import claro.cms.Dao
import claro.cms.util.{Page,AllPages}
import claro.common.util.Conversions._
import claro.jpa.catalog._
import claro.jpa.shop._
import scala.collection.JavaConversions._

object CatalogDao extends Dao {
  
  val dataSource = "claro.jpa.PersistenceUnit"

  private object currentCatalog extends ThreadGlobal[Catalog]

  def withCatalog(catalog : Catalog)(f : => Any) = currentCatalog.doWith(catalog)(f)
  
  def catalog = currentCatalog.value match {
    case null => throw new Exception("No catalog set")
    case catalog => catalog
  }
  
  def category(name : String) : Option[Category] = {
    querySingle(
        "SELECT c " +
        " FROM Category c" + 
        " JOIN c.propertyValues v" +
        " JOIN v.property.labels l" +
        " WHERE l.label = 'Name'" +
        " AND l.language IS NULL" +
        " AND v.stringValue = :name", "name" -> name)
  }
  
  def getOrCreateCategory(name : String) : Category = transaction { em =>
    category(name) match {
      case Some(category) => category
      case None => new Category useIn { category =>
        category.setCatalog(catalog)
        set(category, Properties.name, name)
        em.persist(category)
      }
    }
  }
  
  def product(name : String) : Option[Product] = {
    querySingle(
        "SELECT p " +
        " FROM Product p" + 
        " JOIN p.propertyValues v" +
        " JOIN v.property.labels l" +
        " WHERE l.label = 'Name'" +
        " AND l.language IS NULL" +
        " AND v.stringValue = :name", "name" -> name)
  }
  
  def getOrCreateProduct(name : String) : Product = transaction { em =>
    product(name) match {
      case Some(product) => product
      case None => new Product useIn { product =>
        product.setCatalog(catalog)
        set(product, Properties.name, name)
        em.persist(product)
      }
    }
  }

  def catalog(name : String) : Option[Catalog] = querySingle("SELECT c FROM Catalog c WHERE c.name = :name", "name" -> name)

  def shop(name : String) : Option[Shop] = querySingle("SELECT s FROM Shop s WHERE s.name = :name", "name" -> name)
  
  def getOrCreateShop(name : String) : Shop = {
    shop(name) match {
      case Some(shop) => shop
      case None => new Shop useIn { shop =>
        shop.setName(name)
        shop.setCatalog(catalog)
        catalog.getShops.add(shop)
      }
    }
  }
  
  object Properties {
    def name = getOrCreateProperty(catalog.getRoot, "Name", PropertyType.String)
    def articleNumber = getOrCreateProperty(catalog.getRoot, "ArticleNumber", PropertyType.String)
    def description = getOrCreateProperty(catalog.getRoot, "Description", PropertyType.String)
    def image = getOrCreateProperty(catalog.getRoot, "Image", PropertyType.Media)
    def imageLarge = getOrCreateProperty(catalog.getRoot, "ImageLarge", PropertyType.Media)
    def price = getOrCreateProperty(catalog.getRoot, "Price", PropertyType.Money)
    def synopsis = getOrCreateProperty(catalog.getRoot, "Synopsis", PropertyType.String)
    def product(catalog : Catalog) = {}
  }
  
  def getOrCreateProperty(item : Item, name : String, tpe : PropertyType) : Property = transaction { em =>
    for (property <- item.getProperties) {
      for (label <- property.getLabels) {
        if (label.getLabel == name && label.getLanguage == null) {
          property.setType(tpe)
          return property
        }
      }
    }
    val property = new Property
    item.getProperties.add(property)
    property.setItem(item)
    property.setType(tpe)
    property.setCategoryProperty(false)
    getOrCreateLabel(property, name)
    em.persist(property)
    property
  }
  
  def set(item : Item, property : Property, value : Any, language : String = null) = {
    val propertyValue = item.getPropertyValues.find(v => v.getProperty == property && v.getLanguage == language) getOrElse new PropertyValue useIn(item.getPropertyValues.add(_))
    propertyValue.setItem(item)
    propertyValue.setProperty(property)
    propertyValue.setLanguage(language)
    property.getType match {
      case PropertyType.String => propertyValue.setStringValue(value.asInstanceOf[String])
      case PropertyType.Money => propertyValue.setMoneyValue(value.asInstanceOf[Double])
      case PropertyType.Media => 
        propertyValue.setMediaValue(value.asInstanceOf[Array[Byte]])
        
    }
  }
  
  def setImage(item : Item, property : Property, cl : java.lang.Class[_ <: Any], name : String, language : String = null) = {
    val propertyValue = item.getPropertyValues.find(v => v.getProperty == property && v.getLanguage == language) getOrElse new PropertyValue useIn(item.getPropertyValues.add(_))
    propertyValue.setItem(item)
    propertyValue.setProperty(property)
    propertyValue.setLanguage(language)
    val bytes = cl.getResourceAsStream(name).readBytes
    propertyValue.setMediaValue(bytes)
    if (name.endsWith(".jpg")) {
      propertyValue.setMimeType("image/jpeg")
    }
    if (name.endsWith(".gif")) {
      propertyValue.setMimeType("image/gif")
    }
    if (name.endsWith(".png")) {
      propertyValue.setMimeType("image/png")
    }
  }
  
  
  def getOrCreateLabel(property : Property, name : String, language : String = null) = {
    val label = property.getLabels.find(l => l.getProperty == property && l.getLanguage == language) getOrElse new Label useIn (property.getLabels.add(_))
    label.setLanguage(language)
    label.setLabel(name)
    label.setProperty(property)
    label
  }
  
  def childExtent(items : Set[Item], visited : Set[Item]) : Set[Item] = {
    val parents = items filterNot visited
    if (parents.isEmpty) return items
    val children = query("SELECT child FROM Item child JOIN item.parents parent WHERE parent IN :parents", "parents" -> parents)
    items ++ childExtent(children.toSet, visited ++ parents) 
  }
  
  def findProducts(parents : Iterable[Item], relatedItems : List[Item], page : Page = AllPages) : Set[Product] = {
    childExtent(parents.toSet, Set()).classFilter(classOf[Product])
  }
  
  def createCatalog(catalogName : String) : Catalog = transaction { em =>

    val catalog = querySingle("SELECT c FROM Catalog c WHERE c.name = :name", "name" -> catalogName) match {
      case Some(catalog) => catalog
      case None => new Catalog useIn { catalog =>
        catalog.setName(catalogName)
        em.persist(catalog)
        em.flush

        val root = new Category
        root.setCatalog(catalog)
        catalog.setRoot(root)
        em.persist(root)
        em.flush
      }
    }
    
    withCatalog(catalog) {
      Properties.name
      Properties.articleNumber
      Properties.description
      Properties.image
      Properties.imageLarge
      Properties.price
      Properties.synopsis
      set(catalog.getRoot, Properties.name, "Root")
    }
    em.flush
    catalog
  }
    
  def createInitialCatalog(createInitalData : => Any) : Catalog = transaction { em => 
    querySingle("SELECT c FROM Catalog c") match {
      case Some(catalog) => catalog
      case None => createCatalog("Catalog") useIn (withCatalog(_)(createInitalData))
    }
  }
}