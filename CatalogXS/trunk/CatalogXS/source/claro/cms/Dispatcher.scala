package claro.cms

import net.liftweb.http.{Req,LiftResponse}

object Dispatcher {

  def unapply(req : Req) : Option[LiftResponse] = {
    var result = None
    result
  }
}
