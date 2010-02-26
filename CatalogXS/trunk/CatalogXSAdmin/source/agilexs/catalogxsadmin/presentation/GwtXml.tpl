package agilexs.catalogxsadmin.presentation

import metaphor.psm.gwt.IGwtModule
import metaphor.psm.gwt.deploy.GwtModuleFile

[template CatalogXSAdditionalModuleInheritance(IGwtModule module) constraint module.name.equals("CatalogXSAdmin") joins GwtModuleFile.AdditionalModuleInheritance]
  <!-- any one of the following lines.                        -->
  <inherits name='com.google.gwt.user.theme.standard.Standard' />
[/template]
