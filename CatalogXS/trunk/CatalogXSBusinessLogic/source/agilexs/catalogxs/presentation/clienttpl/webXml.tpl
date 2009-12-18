package agilexs.catalogxs.presentation.clienttpl
    
import metaphor.psm.web.IWebModule
import metaphor.psm.web.WebXml

[template AdditionalWebXmlConfiguration(IWebModule module) joins WebXml.AdditionalConfiguration]
    <filter>
      <filter-name>LiftFilter</filter-name>
      <display-name>Lift Filter</display-name>
      <description>The Filter that intercepts lift calls</description>
      <filter-class>net.liftweb.http.LiftFilter</filter-class>
    </filter>
          
    
    <filter-mapping>
      <filter-name>LiftFilter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>
[/template]
