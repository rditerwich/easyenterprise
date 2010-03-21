package claro.common.util

import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import scala.collection.Map
import claro.common.util.Conversions._

object Locales {

  private val alternativeCache = new ConcurrentHashMap[Locale,List[Locale]]
  
  def getAlternatives(locale : Locale) = {
    alternativeCache getOrElseUpdate (locale, {
      var alternatives = new Locale("") :: Nil
      if (locale.getCountry != "") alternatives = new Locale(locale.getLanguage) :: alternatives
      if (locale.getVariant != "") alternatives = new Locale(locale.getLanguage, locale.getCountry) :: alternatives
      locale :: alternatives
    })
  }
}
