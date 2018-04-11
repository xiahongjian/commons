package tech.hongjian.common.model;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:37:53
 *
 */
public class WebLogInfo {
	private String url;
	private String path;
	private String clientIp;
	private String requestMethod;
	private String clazz;
	private String method;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	@Override
	public String toString() {
		return "WebLogInfo [url=" + url + ", path=" + path + ", clientIp=" + clientIp + ", requestMethod="
				+ requestMethod + ", clazz=" + clazz + ", method=" + method + "]";
	}
}
