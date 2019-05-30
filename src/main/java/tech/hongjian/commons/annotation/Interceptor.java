package tech.hongjian.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author xiahongjian 
 * @time   2018-04-11 14:45:38
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Interceptor {
	/**
	 * 拦截器拦截路径 
	 */
	String[] path();
	
	/**
	 * 用于排序
	 */
	int order();
}
