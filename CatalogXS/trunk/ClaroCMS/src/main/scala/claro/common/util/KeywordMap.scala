package claro.common.util

import scala.collection.{mutable, Set, Map}
import claro.common.util.Conversions._

object KeywordMap {
  def apply[A](keywordMappings : Iterable[(Iterable[String], A)]) = {
	new KeywordMap[A] useIn (result =>
	  for ((keywords, obj) <- keywordMappings) {
	    result.addAll(keywords, obj)
	  })
  }
}

class KeywordMap[A] extends mutable.HashMap[String, mutable.Set[A]] with mutable.MultiMap[String, A] {

  override def addBinding(keyword : String, obj : A) = { 
    for (s <- keyword.toLowerCase.split("\\W"); if s.length > 1) {
    	super.addBinding(s, obj)
    }
    this
  }
  
  def addAll(keywords : Iterable[String], obj : A) = {
    for (keyword <- keywords) {
      add(keyword, obj)
    }
  }
  
  def find(seach : String) : Set[A] = {
    val result = new mutable.HashSet[A]
    for (s <- seach.toLowerCase.split("\\W")) {
      for ((keyword, products) <- this) {
        val maxDist = Math.min(s.length, keyword.length) * 2 / 3
        val dist = distance(s, keyword)
        if (dist < maxDist) {
          result ++= products
        }
      }
    }
    result
  }
  
  private val memo = scala.collection.mutable.Map[(List[Char],List[Char]),Int]()

  def distance(s1 : String, s2 : String) : Int = {
    def min(a:Int, b:Int, c:Int) = Math.min( Math.min( a, b ), c)
    def sd(s1: List[Char], s2: List[Char]): Int = {
      if (memo.contains((s1,s2)) == false)
        memo((s1,s2)) = (s1, s2) match {
          case (_, Nil) => s1.length
          case (Nil, _) => s2.length
          case (c1::t1, c2::t2)  => min( sd(t1,s2) + 1, sd(s1,t2) + 1,
                                       sd(t1,t2) + (if (c1==c2) 0 else 1) )
        }
      memo((s1,s2))
    }
 
    sd( s1.toList, s2.toList )
  }

}
