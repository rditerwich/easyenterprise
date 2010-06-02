package claro.cms

import java.net.URI
import net.liftweb.http.{Bootable, LiftRules,RewriteRequest}
import claro.common.util.Conversions._

class Boot extends Bootable {
  var website : Option[URI] = None
  
  def boot = {
    val websiteUri = LiftRules.context.initParam("website").getOrElse("")

    if (websiteUri == "") {
      throw new Exception("No website specified, please add a 'website' context-param in web.xml")
    }
    Website.register(websiteUri)
    
    LiftRules.dispatch.append(Dispatch)
    LiftRules.viewDispatch.append(ViewDispatch)
    LiftRules.urlDecorate.append(UrlDecorate)
    
    Cms.logger.info(Website.instance.printInfo(""))
  }
  
}
