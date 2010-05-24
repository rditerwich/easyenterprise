package claro.common.util

import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import claro.common.util.Conversions._

object Locales {

  val availableLocales : Map[String, Locale] = Map(Locale.getAvailableLocales.map(l => l.toString -> l):_*)
  
  private val alternativeCache = new ConcurrentHashMap[Locale,List[Locale]]

  def apply(locale : String) = availableLocales.get(locale.getOrElse("")) match {
    case Some(locale) => locale
    case None => new Locale("")
  }
  
  def getAlternatives(locale : Locale) = {
    alternativeCache getOrElseUpdate (locale, {
      var alternatives = new Locale("") :: Nil
      if (locale.getCountry != "") alternatives = new Locale(locale.getLanguage) :: alternatives
      if (locale.getVariant != "") alternatives = new Locale(locale.getLanguage, locale.getCountry) :: alternatives
      locale :: alternatives
    })
  }
}