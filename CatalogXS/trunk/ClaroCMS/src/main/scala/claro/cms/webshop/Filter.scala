package claro.cms.webshop

object Filter {

  def filters : Seq[Filter] = {
    WebshopModel.currentProductGroup match {
      case Some(group) => (WebshopModel.shop.topLevelProductGroups - group).toSeq map (
          new ProductGroupFilter(_))
      case None => Seq()
    }
  }
}

abstract class Filter {
  def title : String
  def values : Seq[FilterValue]
}

abstract class FilterValue {
  def value : String
  def activate : Unit
}

class ProductGroupFilter(group : ProductGroup) extends Filter {
  override def title : String = group.name
  override def values = group.children.toSeq.map(new ProductGroupFilterValue(_))
}

class ProductGroupFilterValue(group : ProductGroup) extends FilterValue {
  override def value : String = group.name
  override def activate : Unit = {}
}
