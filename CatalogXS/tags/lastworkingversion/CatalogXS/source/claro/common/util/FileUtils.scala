package claro.common.util

import java.io.File
import collection.mutable.ArrayBuffer

object SubDirs {
  def apply(dir : String, depth : Int) = scan(new File(dir), depth, new ArrayBuffer[File]).toArray 

  private def scan(dir : File, depth : Int, result : ArrayBuffer[File]) : ArrayBuffer[File] = {
    if (depth >= 0 && dir.isDirectory) {
      result += dir
      for (subdir <- dir.listFiles) {
        scan(subdir, depth - 1, result)
      }
    }
    result
  }
}



