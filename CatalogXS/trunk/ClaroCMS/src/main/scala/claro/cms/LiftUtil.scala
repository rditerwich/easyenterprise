package claro.cms.jscmds

import net.liftweb.http.js.JsCmd

case class ReloadPage() extends JsCmd {
  def toJsCmd = "window.location.href=window.location.href"
}