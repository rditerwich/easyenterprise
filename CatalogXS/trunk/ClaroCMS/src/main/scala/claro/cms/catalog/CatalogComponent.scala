package claro.cms.catalog

import claro.cms.{Cms,Component,Template,ResourceLocator,Scope}
import claro.jpa.catalog._

class CatalogComponent extends Component {

  val prefix = "catalog"
    
  bindings.append {
    case _ : CatalogComponent => Map(
      "products" -> CatalogDao.findProducts(Nil, Nil) -> "product"
    )
    case product : Product => Map(
      "id" -> product.getId
    )
  }
  
  rewrite.append {
     case path => path
  }
  
  override def boot { 
    CatalogDao.createInitialCatalog(StandardCatalogData.createSampleData)
  }
}