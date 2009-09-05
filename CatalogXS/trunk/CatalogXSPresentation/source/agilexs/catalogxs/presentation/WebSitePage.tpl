package agilexs.catalogxs.presentation

import metaphor.psm.gwt.IButton
import metaphor.psm.gwt.client.AttachClickHandler
import metaphor.psm.gwt.client.AttachListeners
import metaphor.psm.gwt.client.RenderContext
import metaphor.tpl.lib.Type

[template ButtonHandler(IButton button, RenderContext context) extends AttachListeners]
    [AttachClickHandler(button, context)]
    [Type("com.google.gwt.user.client.Window")].alert("Button Clicked");
    [/AttachClickHandler]
[/template]