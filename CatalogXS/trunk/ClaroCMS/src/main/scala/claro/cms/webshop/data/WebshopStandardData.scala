package claro.cms.webshop.data

import claro.cms.Cms
import claro.jpa.catalog._
import scala.collection.JavaConversions._

object WebshopStandardData extends WebshopDataFiller {


  def fillDataBase = {
    
    val query = em.createQuery("select from Catalog c where c.name = :name").setParameter("name", "Catlog")
    val result = query.getResultList.asInstanceOf[java.util.List[Catalog]]
    val catalog = if (result.isEmpty) new Catalog else result.get(0)
    catalog.setName("Catalog")
    
    val name = property("Name", PropertyType.String)
    val articleNumber = property("ArticleNumber", PropertyType.String)
    val description = property("Description", PropertyType.String)
    val image = property("Image", PropertyType.Media)
    val imageLarge = property("ImageLarge", PropertyType.Media)
    val price = property("Price", PropertyType.Money)
    val synopsis = property("Synopsis", PropertyType.String)

    val root = new ProductGroup
    root.setId(1)
    root.getProperties.add(name)
    root.getProperties.add(articleNumber)
    root.getProperties.add(description)
    root.getProperties.add(image)
    root.getProperties.add(imageLarge)
    root.getProperties.add(price)
    root.getProperties.add(synopsis)
    set(root, name, "Root")

    
  }
}