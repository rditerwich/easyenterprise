package claro.common.util

import java.io.{ByteArrayOutputStream,InputStream, BufferedInputStream,InputStreamReader}
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
  
  def readBytes : Array[Byte] = {
    val is = new BufferedInputStream(getInputStream)
    val result = new ByteArrayOutputStream()
    val buffer = new Array[Byte](1000)
    try {
      var n = 0
      while (n >= 0) {
        result.write(buffer, 0, n)
        n = is.read(buffer)
      }
    } finally {
      result.close
      is.close
    }
    result.toByteArray
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
