package agilexs.catalogxsadmin.presentation.server;

import java.io.IOException;
import java.lang.Long;
import java.lang.String;
import java.lang.Throwable;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import agilexs.catalogxsadmin.businesslogic.Catalog;
import agilexs.catalogxsadmin.jpa.catalog.Item;
import agilexs.catalogxsadmin.jpa.catalog.Property;
import agilexs.catalogxsadmin.jpa.catalog.PropertyValue;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadMediaServlet2 extends HttpServlet {
    private static final long serialVersionUID = 0L;


    @EJB
    private Catalog catalogSessionBean;
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        try {
            if (!ServletFileUpload.isMultipartContent(req)) {
                resp.getWriter().write("Error: Content is not encoded as multipart");
                return;
            }
            final DiskFileItemFactory factory = new DiskFileItemFactory();
            final ServletFileUpload upload = new ServletFileUpload(factory);
            final List<FileItem> fileItems = upload.parseRequest(req);

            store(resp, fileItems);
        }
        catch (Throwable t) {
            resp.getWriter().write("Error: " + t.toString());
        }
    }

    private void store(HttpServletResponse resp, List<FileItem> fileItems) {
        final HashMap<String, FileItem> map = new HashMap<String, FileItem>();

        for (FileItem fileItem : fileItems) {
         map.put(fileItem.getFieldName(), fileItem);
        }
        try {
            final String pvIdS = (new String(map.get("propertyValueId").get())).trim();
            final Long pvId = "".equals(pvIdS) ? null : Long.valueOf(pvIdS);
            final Long propertyId = Long.valueOf(new String(map.get("propertyId").get()));
            final Long itemId = Long.valueOf(new String(map.get("itemId").get()));
            final String lang = new String(map.get("language").get());
            final FileItem file = map.get("fileupload");
            final PropertyValue pv = new PropertyValue();
            final String fileName = file.getName();
            final int li =
                fileName.lastIndexOf('/') > -1 ? fileName.lastIndexOf('/') :
                (fileName.lastIndexOf('\\') > -1 ? fileName.lastIndexOf('\\') : 0);
            final PropertyValue oldPv = pvId == null ? null : catalogSessionBean.findPropertyValueById(pvId);
            final Item item = new Item();
            item.setId(itemId);
            pv.setItem(item);

            final Property property = new Property();
            property.setId(propertyId);
            pv.setProperty(property);
            pv.setMimeType(file.getContentType());
            if (!"".equals(lang)) {
                pv.setLanguage(lang);
            }
            pv.setStringValue(fileName.substring(li));
            pv.setMediaValue(file.get());
            if (itemId != null) {
                final PropertyValue npv = catalogSessionBean.updatePropertyValue(oldPv, pv);
                resp.getWriter().write(npv.getId() + "");
            }
        } catch (Exception e) {
        }
    }
}