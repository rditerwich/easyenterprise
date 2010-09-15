package claro.cms

import _root_.scala.xml.{Elem, NodeSeq}

import _root_.net.liftweb.http.S
import _root_.net.liftweb.http.js.JE
import _root_.net.liftweb.http.js.JsCmds
import net.liftweb.http._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery._
import JE._
import JqJE._
import net.liftweb.util.Helpers._


object MyJsArtifacts extends JSArtifacts {


  def toggle(id: String) = JqId(id) ~> new JsMethod {
    def toJsCmd = "toggle()"
  }

  def hide(id: String) = JqId(id) ~> new JsMethod {
    def toJsCmd = "hide()"
  }

  def show(id: String) = JqId(id) ~> new JsMethod {
    def toJsCmd = "show()"
  }

  def showAndFocus(id: String) = JqId(id) ~> new JsMethod {
    def toJsCmd = "show().each(function(i) {var t = this; setTimeout(function() { t.focus(); }, 200);})"
  }

  def serialize(id: String) = JqId(id) ~> new JsMethod {
    def toJsCmd = "serialize()"
  }

  def setHtml(id: String, xml: NodeSeq): JsCmd = JqJsCmds.JqSetHtml(id, xml)

  def onLoad(cmd: JsCmd): JsCmd = JqJsCmds.JqOnLoad(cmd)

  def ajax(data: AjaxInfo): String = {
    "jQuery.ajax(" + toJson(data, Request.get.context.openOr(""),
                            prefix =>
                            JsRaw(S.encodeURL(prefix + "/" +LiftRules.ajaxPath + "/").encJs))+");"
  }

  def comet(data: AjaxInfo): String = {
    "jQuery.ajax(" + toJson(data, LiftRules.cometServer(), LiftRules.calcCometPath) + ");"
  }

  def jsonStringify(in: JsExp) : JsExp = new JsExp {
    def toJsCmd = "JSON.stringify(" + in.toJsCmd + ")"
  }

  def formToJSON(formId: String):JsExp = new JsExp() {
    def toJsCmd = "lift$.formToJSON('" + formId + "')";
  }

  private def toJson(info: AjaxInfo, server: String, path: String => JsExp): String =
  (("url : addPageName(" + path(server).toJsCmd + ")" ) ::
   "data : " + info.data.toJsCmd ::
   ("type : " + info.action.encJs) ::
   ("dataType : " + info.dataType.encJs) ::
   "timeout : " + info.timeout ::
   "cache : " + info.cache :: Nil) ++
  info.successFunc.map("success : " + _).toList ++
  info.failFunc.map("error : " + _).toList mkString("{ ", ", ", " }")
}
