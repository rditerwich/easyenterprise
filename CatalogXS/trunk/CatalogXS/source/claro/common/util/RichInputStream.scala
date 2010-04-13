package claro.common.util

import java.io.{InputStream,InputStreamReader}
import java.nio.charset.Charset

class RichInputStream(is : InputStream) {

  def readString : String = readString(Charset.forName("UTF-8")) 
	  
  def readString(charset : Charset) : String = {
    val reader = new InputStreamReader(is, charset)
    val buffer = new Array[Char](1000)
    var result = new StringBuilder
    try {
      var read = 0
      while (read >= 0) {
    	read = reader.read(buffer)
    	result.append(buffer, 0, read)
      }
    } finally {
      reader.close
    }
    result.toString
  }
}
