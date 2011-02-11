package easyenterprise.lib.server;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestService {

	private static ThreadLocal<HttpServletRequest> httpRequest = new ThreadLocal<HttpServletRequest>();

	public static void setRequest(HttpServletRequest request) {
		httpRequest.set(request);
	}
	
	public static HttpServletRequest getRequest() {
		return httpRequest.get();
	}
	
}
