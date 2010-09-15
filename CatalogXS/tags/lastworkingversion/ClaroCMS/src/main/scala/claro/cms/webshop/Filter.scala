package claro.cms.webshop

object Filter {

  def filters : Seq[Filter] = {
    WebshopModel.currentCategory match {
      case Some(group) => (WebshopModel.shop.topLevelCategories - group).toSeq map (
          new CategoryFilter(_))
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

class CategoryFilter(group : Category) extends Filter {
  override def title : String = group.name
  override def values = group.children.toSeq.map(new CategoryFilterValue(_))
}

class CategoryFilterValue(group : Category) extends FilterValue {
  override def value : String = group.name
  override def activate : Unit = {}
}
