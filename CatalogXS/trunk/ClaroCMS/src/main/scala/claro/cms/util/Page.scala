package claro.cms.util

object AllPages extends Page(0, 999999)
case class Page(start : Int, size : Int)