package claro.common.util

import java.io.{InputStream,InputStreamReader}
import java.nio.charset.Charset

class RichInputStream(getInputStream : => InputStream) {

  def readString : String = readString(Charset.forName("UTF-8")) 
	  
  def readString(charset : Charset) : String = {
    val reader = new InputStreamReader(getInputStream, charset)
    val buffer = new Array[Char](1000)
    var result = new StringBuilder
    try {
      var n = 0
      while (n >= 0) {
        result.append(buffer, 0, n)
        n = reader.read(buffer)
      }
    } finally {
      reader.close
    }
    result.toString
  }
  
  def exists : Boolean = {
    try {
      val is : InputStream = getInputStream
      is != null
    } catch {
      case _ => false
    }
  }
}
