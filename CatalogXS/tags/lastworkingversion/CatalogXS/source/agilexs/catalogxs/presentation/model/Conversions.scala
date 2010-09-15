package agilexs.catalogxs.presentation.model

import scala.collection.jcl
import scala.collection.Set
import scala.collection.Map
import scala.collection.immutable
import scala.collection.mutable
import scala.xml.NodeSeq 
import net.liftweb.util.BindHelpers.AttrBindParam
import net.liftweb.util.BindHelpers.FuncBindParam
import claro.common.util.Conversions._

object Conversions {
  


  /**
   * Implicit conversions for binding objects
   */
  implicit def stringToBindingsWithTag[A](t : Tuple2[Tuple2[String, Function2[String, NodeSeq, NodeSeq]],String]) = 
	FuncBindParam(t._1._1, (xml) => t._1._2(t._2, xml))

  implicit def linkAttrBindParam[A](t : Tuple2[Tuple2[String, LinkAttr],String]) = 
	AttrBindParam(t._1._1, t._1._2.value, t._2)

  implicit def toBindableObject(obj : Object) = new BindableObject(obj)
}

