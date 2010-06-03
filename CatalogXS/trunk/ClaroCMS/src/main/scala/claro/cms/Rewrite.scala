package claro.cms

import net.liftweb.http.{LiftRules,RewriteRequest,RewriteResponse,ParsePath}
import claro.common.util.Locales

object Rewrite extends LiftRules.RewritePF {

  def isDefinedAt(request : RewriteRequest) = {
    !request.path.partPath.isEmpty && Website.instance.config.locales.contains(request.path.partPath.head) 
  }
  
  def apply(request : RewriteRequest) : RewriteResponse = {
    val parsePath = request.path
    Cms.locale.set(Locales(parsePath.partPath.head))
    RewriteResponse(ParsePath(parsePath.partPath.tail, parsePath.suffix, parsePath.absolute, parsePath.endSlash), Map.empty, true)
  }
}