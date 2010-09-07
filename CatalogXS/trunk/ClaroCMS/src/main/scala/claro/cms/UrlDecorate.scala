package claro.cms

import java.util.Locale
import net.liftweb.http.LiftRules

object UrlDecorate extends LiftRules.URLDecoratorPF {

  def isDefinedAt(url : String) = true
  
  def apply(url : String) : String = {
    val locale = Cms.locale.is
    if (locale == Website.instance.config.defaultLocale) url
    else if (locale.toString != "") "/" + locale + url
    else url
    LiftRules.context.path + "/" + locale
  }
}
