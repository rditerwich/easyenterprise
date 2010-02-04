package agilexs.catalogxs.presentation.model

import net.liftweb.util.BindHelpers
import net.liftweb.util.Helpers._

object Tag {
  implicit def tagToString(tag: Tag) = tag or ""
}

sealed case class Tag(tag: String) {
  def or (default: String) = {
    tag match {
      case null => BindHelpers.attr("tag") match { 
	      case Some(tag) => tag openOr default 
	      case None => default 
	    }
      case tag => tag
    }
  }
}

final case object DefaultTag extends Tag(null) {
  
}


