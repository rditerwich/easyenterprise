package claro.cms.catalog

import claro.cms.{Cms,Dao}
import claro.jpa.catalog._
import claro.common.util.Conversions._
import scala.collection.JavaConversions._

object StandardCatalogData {

  val dao = CatalogDao
  
  def fillDatabase = {

    val catalog = dao.querySingle("select from Catalog c where c.name = :name", "name" -> "Catlog") match {
      case Some(catalog) => catalog
      case None => new Catalog useIn (_.setName("Catalog"))
    }
    
    val root : ProductGroup = dao.querySingle("select from ProductGroup g where g.id = :id", "id" -> 1l) match {
    case Some(root) => root
    case None => new ProductGroup useIn (_.setId(1l))
    }
    
    val name = dao.property(root, "Name", PropertyType.String)
    val articleNumber = dao.property(root, "ArticleNumber", PropertyType.String)
    val description = dao.property(root, "Description", PropertyType.String)
    val image = dao.property(root, "Image", PropertyType.Media)
    val imageLarge = dao.property(root, "ImageLarge", PropertyType.Media)
    val price = dao.property(root, "Price", PropertyType.Money)
    val synopsis = dao.property(root, "Synopsis", PropertyType.String)

    dao.set(root, name, "Root")

    
  }
}