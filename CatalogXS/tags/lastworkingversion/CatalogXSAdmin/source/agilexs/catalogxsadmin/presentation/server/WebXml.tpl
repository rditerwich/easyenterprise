package agilexs.catalogxsadmin.presentation.server

import metaphor.core.IModule
import metaphor.psm.javaee.deploy.WebXml

[template AdditionalWebXmlConfiguration(IModule module) joins WebXml.AdditionalConfiguration]
    <servlet-mapping>
        <servlet-name>CatalogXSUploadMedia</servlet-name>
        <url-pattern>/CatalogXSAdmin/UploadMedia</url-pattern>
    </servlet-mapping>
    <servlet>
        <servlet-name>CatalogXSUploadMedia</servlet-name>
        <servlet-class>agilexs.catalogxsadmin.presentation.server.UploadMediaServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>CatalogXSDownloadMedia</servlet-name>
        <url-pattern>/CatalogXSAdmin/DownloadMedia</url-pattern>
    </servlet-mapping>
    <servlet>
        <servlet-name>CatalogXSDownloadMedia</servlet-name>
        <servlet-class>agilexs.catalogxsadmin.presentation.server.DownloadMediaServlet</servlet-class>
    </servlet>
[/template]