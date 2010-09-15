package claro.cms

import java.util.Locale
import net.liftweb.http.{S,LiftRules}
import net.liftweb.common.{Full,Empty}

object UrlDecorate extends LiftRules.URLDecoratorPF {

  def isDefinedAt(url : String) = true
  
  def apply(url : String) : String = {
    val locale = Cms.locale.is
    if (locale == Website.instance.config.defaultLocale) url
    else if (locale.toString != "") "/" + locale + url
    else url
  }
}

