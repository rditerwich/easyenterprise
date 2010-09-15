package agilexs.catalogxs.presentation.util

import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
 
import js._ 
import JsCmds._ 
 
import mapper._ 
 
import util._ 
import Helpers._

import scala.collection.jcl.{BufferWrapper,SetWrapper} 

import net.liftweb.http.{Req, GetRequest, PostRequest, LiftRules,
                         NotFoundResponse, InMemoryResponse}
import net.liftweb.http.js.JE._

import agilexs.catalogxs.presentation.model._
import agilexs.catalogxs.presentation.model.Conversions._

/**
 * Displays an image present in the database in a propertyValue. The image
 * is retrieved by doing a get /image/123 where 123 is the propertyValue id.
 */
object ImageDispatcher {
  def dispatch: LiftRules.DispatchPF = {
    // Req(url_pattern_list, suffix, request_type)
    case Req(List("image"), _, _) => () => Full(get)
  }
  
  def get = {
    Model.shop.mediaValues.get(S.param("imageID").openOr("0").toLong) match {
	  case Some((mimeType, image)) => ImageResponse(mimeType, image)
      case _ =>  NotFoundResponse()
    }
  }
}

case class ImageResponse(mimeType : String, bytes : Array[Byte]) extends LiftResponse with HeaderStuff {  
  def toResponse =
    InMemoryResponse(bytes, ("Content-Length", bytes.length.toString) :: 
                     ("Content-Type", mimeType) :: headers, cookies, 200)  
}
