package tech.hongjian.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.slf4j.event.Level;

/**
 * 方法性能检测，在日志里记录方法执行耗时
 * 
 * @author xiahongjian 
 * @time   2018-04-11 10:59:09
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Performance {
	/**
	 * 时间单位 ,仅支持毫秒、秒、分,其他单位将会当做毫秒处理
	 */
	TimeUnit value() default TimeUnit.MILLISECONDS;
	
	/**
	 * 输出日志level，默认为debug
	 */
	Level level() default Level.DEBUG;
}
