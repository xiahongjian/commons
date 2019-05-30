package tech.hongjian.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/** 
 * @author xiahongjian
 * @time   2019-05-30 21:06:51
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface FMMethod {
    /**
     * freemarker方法名称
     */
    String value() default "";
}

