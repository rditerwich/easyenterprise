package claro.cms

import java.util.Locale
import net.liftweb.http.{Req,LiftRules,LiftResponse,CSSResponse,StreamingResponse,InMemoryResponse,JavaScriptResponse,NotFoundResponse,ResourceServer}
import net.liftweb.common.{Box,Full,Empty}
import net.liftweb.util.{CSSParser,Log}
import net.liftweb.util.TimeHelpers._
import claro.common.util.Conversions._

object UrlDecorate extends LiftRules.URLDecoratorPF {

  val emptyResponse : () => Box[LiftResponse] = () => Empty
  
  def isDefinedAt(url : String) = true
  
  def apply(url : String) : String = {
    val locale = Cms.locale.is
    if (locale == Website.instance.config.defaultLocale) url
    else "/" + locale + url
  }
}
