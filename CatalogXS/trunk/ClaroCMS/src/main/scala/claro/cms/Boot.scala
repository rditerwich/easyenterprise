package claro.cms
import claro.common.util.Conversions._

import java.net.URI
import net.liftweb.http.{Bootable, LiftRules,NoticeType}
import net.liftweb.common.{Full}
import net.liftweb.util.{Helpers}

class Boot extends Bootable  {
	
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
    LiftRules.rewrite.append(Rewrite)
    Cms.logger.info(Website.instance.printInfo(""))
  
    import Helpers._
    LiftRules.noticesAutoFadeOut.default.set((noticeType: NoticeType.Value) => Full((2 seconds, longToTimeSpan(500l))))
  }
  
}
