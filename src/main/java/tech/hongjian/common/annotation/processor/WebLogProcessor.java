package tech.hongjian.common.annotation.processor;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tech.hongjian.common.annotation.WebLog;
import tech.hongjian.common.model.WebLogInfo;
import tech.hongjian.common.util.WebUtil;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:36:13
 *
 */
@Aspect
public class WebLogProcessor {
	private LogService logService;
	
	public WebLogProcessor() {
		this(new DefaultLogService());
	}
	
	public WebLogProcessor(LogService logService) {
		this.logService = logService;
	}
	
	@Before("@annotation(webLog)")
	public void processWebLog(JoinPoint joinPoint, WebLog webLog) {
		Signature signature = joinPoint.getSignature();
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		WebLogInfo info = new WebLogInfo();
		info.setUrl(request.getRequestURL().toString());
		info.setPath(request.getRequestURI());
		info.setRequestMethod(request.getMethod());
		info.setClientIp(WebUtil.getRealIp(request));
		info.setClazz(signature.getDeclaringTypeName());
		info.setMethod(signature.getName());
		logService.log(info);
	}
	
	
	
	
	static class DefaultLogService implements LogService {
		private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogService.class);
		@Override
		public void log(WebLogInfo info) {
			LOGGER.info(info.toString());
		}
	}
}
