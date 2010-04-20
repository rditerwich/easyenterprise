package claro.cms

import claro.common.util.Conversions._

object CmsInfo {

  def websiteInfo(website : Website, prefix : String) = "\n" + prefix + "Web site: " +  
    website.config.name.emptyOrPrefix("\n" + prefix + "  Name: ") + 
    website.server.emptyOrPrefix("\n" + prefix + "  Server: ") + 
    website.contextPath.emptyOrPrefix("\n" + prefix + "  Context: ") + 
    (website.config.parents.map(_.id) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Parent: " + single
      case many => "\n" + prefix + "  Parents:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) + 
    (website.locations.map(_.toString) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Location: " + single
      case many => "\n" + prefix + "  Locations:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) + 
    (website.emProperties.map(e => e._1 + "=" + e._2) match {
    case Nil => ""
    case many => "\n" + prefix + "  Entity Manager:\n    " + prefix + many.mkString("\n" + prefix + "    ")
    }) 

}
