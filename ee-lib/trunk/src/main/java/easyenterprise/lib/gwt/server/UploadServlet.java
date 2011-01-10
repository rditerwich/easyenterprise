package easyenterprise.lib.gwt.server;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

public class UploadServlet extends gwtupload.server.UploadServlet {

	private static final long serialVersionUID = 1L;
	
	protected static final ThreadLocal<List<FileItem>> currentFileItems = new ThreadLocal<List<FileItem>>() {
  	protected java.util.List<FileItem> initialValue() {
  		return Collections.emptyList();
  		}
  };

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.doGet(request, response);
		currentFileItems.set(UploadServlet.getSessionFileItems(request));
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.doPost(request, response);
		perThreadRequest.set(request);
		currentFileItems.set(UploadServlet.getSessionFileItems(request));
	}

	public static FileItem getUploadedFile(String fieldName) {
		return findItemByFieldName(currentFileItems.get(), fieldName);
	}
}
