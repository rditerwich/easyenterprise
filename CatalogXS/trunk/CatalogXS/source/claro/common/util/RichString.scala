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
  
  def afterLast(pattern : String) : String = {
    val source = getOrElse("")
  	val index = source.lastIndexOf(pattern);
   	if (index < 0) source
    else source.substring(index + pattern.length());
  }

  def afterLast(c : Char) : String = {
    val source = getOrElse("")
	val index = source.lastIndexOf(c);
	if (index < 0) source
	else source.substring(index + 1);
  }
  
  def dropPrefix(prefix : String) : String = {
	if (!s.startsWith(prefix)) s
	else s.substring(prefix.length)
  }
  
  def dropSuffix(suffix : String) : String = {
    if (!s.endsWith(suffix)) s
    else s.substring(0, s.length - suffix.length)
  }

  def ensurePrefix(prefix : String) : String = {
	if (s.startsWith(prefix)) s
	else prefix + s
  }
  
  def ensureSuffix(suffix : String) : String = {
    if (s.endsWith(suffix)) s
    else s + suffix
  }

  
}

