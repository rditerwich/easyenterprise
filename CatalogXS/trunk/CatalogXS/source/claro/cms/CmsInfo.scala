package claro.cms

import claro.common.util.Conversions._

object CmsInfo {

  def siteInfo(site : Site, prefix : String) = "\n" + prefix + "Site: " +  
    site.config.name.emptyOrPrefix("\n" + prefix + "  Name: ") + 
    site.server.emptyOrPrefix("\n" + prefix + "  Server: ") + 
    site.contextPath.emptyOrPrefix("\n" + prefix + "  Context: ") + 
    (site.config.parents.map(_.id) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Parent: " + single
      case many => "\n" + prefix + "  Parents:\n    " + many.mkString("\n" + prefix + "    ")
    }) + 
    (site.locations.map(_.toString) match {
      case Nil => ""
      case single :: Nil => "\n" + prefix + "  Location: " + single
      case many => "\n" + prefix + "  Locations:\n    " + many.mkString("\n" + prefix + "    ")
    }) 

}
