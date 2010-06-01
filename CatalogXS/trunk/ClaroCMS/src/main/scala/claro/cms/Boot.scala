package claro.cms

import java.net.URI
import net.liftweb.http.{Bootable, LiftRules}
import claro.common.util.Conversions._

class Boot extends Bootable {
  var website : Option[URI] = None
  
  def boot = {
    val websiteUri = new URI(LiftRules.context.initParam("website").openOr(""))

    if (websiteUri.getPath == "") {
      Cms.logger.error("No website specified, please add a 'website' context-param in web.xml")
    } else if (!websiteUri.exists) {
      Cms.logger.error("Website configuration file '" + websiteUri + "' does not exist")
    } else {
      LiftRules.statelessDispatchTable.append(Dispatch)
      LiftRules.viewDispatch.append(ViewDispatch)
//      LiftRules.rewrite.append {
//		case RewriteRequest(
//		ParsePath(List("account",acctName),_,_,_),_,_) =>
//		RewriteResponse("viewAcct" :: Nil, Map("name" -> acctName))
//		case RewriteRequest(
//		ParsePath(List("account",acctName, tag),_,_,_),_,_) =>
//		RewriteResponse("viewAcct" :: Nil, Map("name" -> acctName,
//		"tag" -> tag)))
//		}
      Cms.website = new Website(websiteUri)
      Cms.logger.info(Cms.website.printInfo(""))
    }
  }
}
