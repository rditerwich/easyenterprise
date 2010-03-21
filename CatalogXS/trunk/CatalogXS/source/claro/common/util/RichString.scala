package claro.common.util

/**
 * Extensions to the String class
 */
class RichString(s: String) {
  def getOrElse (s2: String) = {
	if (s == null || s.trim() == "") s2 
    else s
  }
  def parsePrefix (prefix : String) : Option[String] = {
    if (s.startsWith(prefix)) Some(s.substring(prefix.length))
    else None
  }
}

