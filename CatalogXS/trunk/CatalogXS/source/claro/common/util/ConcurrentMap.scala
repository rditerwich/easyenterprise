package claro.common.util

import java.util.concurrent.ConcurrentMap

object MapMaker {

  sealed trait RefType
  object WEAK extends RefType
  object SOFT extends RefType
  object HARD extends RefType
  
  def apply[K,V](concurrencyLevel : Int, keyType : RefType, valueType : RefType, f : K => V) : ConcurrentMap[K, V] = {
    var mapMaker = new com.google.common.collect.MapMaker().concurrencyLevel(concurrencyLevel)
    mapMaker = keyType match {
      case WEAK => mapMaker.weakKeys
      case SOFT => mapMaker.softKeys
      case HARD => mapMaker
    } 
    mapMaker = valueType match {
      case WEAK => mapMaker.weakValues
      case SOFT => mapMaker.softValues
      case HARD => mapMaker
    } 
    mapMaker.makeComputingMap[K,V](
      new com.google.common.base.Function[K,V] {
        def apply(key : K) : V = f(key)
      });
  }
}
