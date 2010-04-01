package claro.common.util

/**
 * Extensions to the String class
 */
class RichString(s: String) {

  def asOption = {
	if (s == null || s.trim() == "") None 
    else Some(s)
  } 
  
  def getOrElse (s2: String) = {
	if (s == null || s.trim() == "") s2 
    else s
  }
  def emptyOrPrefix(s1 : String) = {
	if (s == null || s.trim() == "") "" 
	else s1 + s 
  }
  def emptyOrPostfix(s1 : String) = {
	if (s == null || s.trim() == "") "" 
	else s + s1  
  }
  def emptyOrSurround(s1 : String, s2 : String) = {
	if (s == null || s.trim() == "") "" 
    else s1 + s + s2
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

class RichStringSeq(ss : Seq[String]) {
  def trim : Seq[String] = ss map (_.trim) filter(!_.isEmpty) 
}

class RichStringList(ss : List[String]) {
  def trim : List[String] = ss map (_.trim) filter(!_.isEmpty) 
}
