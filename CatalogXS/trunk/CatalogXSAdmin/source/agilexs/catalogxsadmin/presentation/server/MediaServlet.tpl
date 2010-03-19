package agilexs.catalogxsadmin.presentation.server

import metaphor.psm.domaintogwt.ejb.SessionBeanField
import metaphor.psm.ejb.ISessionBean
import metaphor.psm.gwt.IGwtFileDownloadServlet
import metaphor.psm.gwt.IGwtFileUploadServlet
import metaphor.psm.gwt.server.GwtFileDownloadRetrieveMethodImplementation
import metaphor.psm.gwt.server.GwtFileUploadStoreMethodImplementation
import metaphor.tpl.lib.Type
import mof.IRepository

method ISessionBean GetSessionBean(IRepository repository, String sessionBeanName)
  var String fullPath is "/packages:AgileXS/packages:CatalogXSAdmin/packages:BusinessLogic/beans:" + sessionBeanName

  return repository.object(fullPath)
/method

[template DownloadMediaGwtFileDownloadRetrieveMethodImplementation (IGwtFileDownloadServlet servlet) extends GwtFileDownloadRetrieveMethodImplementation]
    [var ISessionBean catalogBean is GetSessionBean(servlet.repository(), "Catalog")]
    final FileData fd = new FileData();

    try {
        final [Type("java.lang.Long")] pvId = [Type("java.lang.Long")].valueOf(req.getParameter("pvId"));
        final [Type("agilexs.catalogxsadmin.jpa.catalog.PropertyValue")] pv = [SessionBeanField(catalogBean)].findPropertyValueById(pvId);

        if (pv != null) {
            resp.addHeader("Content-disposition", "attachment; filename=" + pv.getStringValue());
            fd.content = pv.getMediaValue();
            fd.contentType = pv.getMimeType();
        } else {
            fd.content = ("Could not find media file in database (id:" + pvId +")").getBytes();
            fd.contentType = "image/png";
        }
    } catch (Exception e) {
        fd.content = "Error finding media file in database ".getBytes();
        fd.contentType = "image/png";
    }
    return fd;
[/template]

[template UploadMediaGwtFileUploadStoreMethodImplementation (IGwtFileUploadServlet servlet) extends GwtFileUploadStoreMethodImplementation]
    [var ISessionBean catalogBean is GetSessionBean(servlet.repository(), "Catalog")]
    final [Type("java.util.HashMap")]<[Type("java.lang.String")], [Type("org.apache.commons.fileupload.FileItem")]> map = new [Type("java.util.HashMap")]<[Type("java.lang.String")], [Type("org.apache.commons.fileupload.FileItem")]>();

    for ([Type("org.apache.commons.fileupload.FileItem")] fileItem : fileItems) {
     map.put(fileItem.getFieldName(), fileItem);
    }
    try {
        final [Type("java.lang.String")] pvIdS = (new [Type("java.lang.String")](map.get("propertyValueId").get())).trim();
        final [Type("java.lang.Long")] pvId = "".equals(pvIdS) ? null : Long.valueOf(pvIdS);
        final [Type("java.lang.Long")] propertyId = [Type("java.lang.Long")].valueOf(new [Type("java.lang.String")](map.get("propertyId").get()));
        final [Type("java.lang.Long")] itemId = [Type("java.lang.Long")].valueOf(new [Type("java.lang.String")](map.get("itemId").get()));
        final FileItem file = map.get("fileupload");
        final [Type("agilexs.catalogxsadmin.jpa.catalog.PropertyValue")] pv = new [Type("agilexs.catalogxsadmin.jpa.catalog.PropertyValue")]();
        final String fileName = file.getName();
        final int li =
            fileName.lastIndexOf('/') > -1 ? fileName.lastIndexOf('/') :
            (fileName.lastIndexOf('\\') > -1 ? fileName.lastIndexOf('\\') : 0);
        final [Type("agilexs.catalogxsadmin.jpa.catalog.PropertyValue")] oldPv = pvId == null ? null : [SessionBeanField(catalogBean)].findPropertyValueById(pvId);

        pv.setItem([SessionBeanField(catalogBean)].findItemById(itemId));
        pv.setProperty([SessionBeanField(catalogBean)].findPropertyById(propertyId));
        pv.setMimeType(file.getContentType());
        pv.setStringValue(fileName.substring(0));
        pv.setMediaValue(file.get());
        if (itemId != null) {
            [SessionBeanField(catalogBean)].updatePropertyValue(oldPv, pv);
        }
    } catch (Exception e) {
    }
[/template]
