package tech.hongjian.commons.web;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:57:39
 *
 */
public class WebUtil {
	/**
	 * 获取请求反向代理之前的IP
	 */
	public static String getClientRealIp(HttpServletRequest request) {
		String header = request.getHeader("x-forwarded-for");
		if (isCorrectIP(header)) {
			// 多次反向代理后会出现如“10.47.103.13,4.2.2.2,10.96.112.230”的情况，
			// 第一个IP为客户端真实IP
			return header.indexOf(",") == -1 ? header : header.split(",")[0];
		}
		
		header = request.getHeader("Proxy-Client-IP");
		if (isCorrectIP(header))
			return header;
		
		header = request.getHeader("WL-Proxy-Client-IP");
		if (isCorrectIP(header))
			return header;
		
		header = request.getHeader("TTP_CLIENT_IP");
		if (isCorrectIP(header))
			return header;
		
		header = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (isCorrectIP(header))
			return header;
		
		header = request.getHeader("X-Real-IP");
		if (isCorrectIP(header))
			return header;
		
		return request.getRemoteAddr();
	}
	
	private static boolean isCorrectIP(String str) {
		return str != null && !"".equals(str) && !"unknown".equalsIgnoreCase(str);
	}
}
