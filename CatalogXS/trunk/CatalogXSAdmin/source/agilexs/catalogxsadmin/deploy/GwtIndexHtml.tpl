package agilexs.catalogxsadmin.deploy

import metaphor.psm.gwt.IGwtModule
import metaphor.psm.gwt.IGwtWebConfiguration
import metaphor.psm.gwt.deploy.GwtDocType
import metaphor.workspace.IProject

[template LayoutGwtDocType(IGwtWebConfiguration gwtConfig, IGwtModule module, IProject project) extends GwtDocType]
    <!DOCTYPE html>
[/template]