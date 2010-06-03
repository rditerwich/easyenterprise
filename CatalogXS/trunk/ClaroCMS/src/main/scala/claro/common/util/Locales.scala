package claro.common.util

import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import claro.common.util.Conversions._
import scala.collection.JavaConversions._

object Locales {

  val availableLocales : Map[String, Locale] = Map(Locale.getAvailableLocales.map(l => l.toString -> l):_*)
  
  private val alternativeCache = new ConcurrentHashMap[Locale,List[Locale]]

  val empty = new Locale("")
  
  def apply(locale : String) = availableLocales.get(locale.getOrElse("")) match {
    case Some(locale) => locale
    case None => empty
  }
  
  def getAlternatives(locale : Locale) = {
    alternativeCache getOrElseUpdate (locale, {
      var alternatives = empty :: Nil
      if (locale.getCountry != "") alternatives = new Locale(locale.getLanguage) :: alternatives
      if (locale.getVariant != "") alternatives = new Locale(locale.getLanguage, locale.getCountry) :: alternatives
      if (locale == empty) empty :: Nil
      else locale :: alternatives
    })
  }
}