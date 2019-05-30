package tech.hongjian.commons.web.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import tech.hongjian.commons.annotation.FMDirective;
import tech.hongjian.commons.annotation.FMMethod;

/** 
 * @author xiahongjian
 * @time   2019-05-30 21:02:14
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(value = {FreeMarkerConfigurer.class, FreeMarkerViewResolver.class})
public class FreeMarkerConfig {
    @Autowired
    private ApplicationContext appCtx;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private FreeMarkerViewResolver freeMarkerViewResolver;
    
    @Bean
    public FreeMarkerConfig init() {
     // 配置自定义指令
        setCustomDirective();
        // 配置自定方法
        setCustomMethod();
        return null;
    }

    private void setCustomDirective() {
        freemarker.template.Configuration configuration = freeMarkerConfigurer.getConfiguration();

        Map<String, Object> directiveMap = appCtx.getBeansWithAnnotation(FMDirective.class);
        directiveMap.entrySet().forEach(entry -> {
            Object value = entry.getValue();
            if (!(value instanceof TemplateModel))
                return;
            String name = getDirectiveName(value);
            configuration.setSharedVariable(name, (TemplateModel) value);

        });
    }

    private void setCustomMethod() {
        if (freeMarkerViewResolver == null)
            return;
        Map<String, Object> methodMap = appCtx.getBeansWithAnnotation(FMMethod.class);
        Map<String, TemplateMethodModelEx> map = new HashMap<>(methodMap.size());
        methodMap.entrySet().forEach(entry -> {
            Object value = entry.getValue();
            if (!(value instanceof TemplateMethodModelEx))
                return;
            String name = getMethodName(value);
            map.put(name, (TemplateMethodModelEx) value);
        });
        freeMarkerViewResolver.setAttributesMap(map);
    }

    private String getMethodName(Object bean) {
        FMMethod method = bean.getClass().getAnnotation(FMMethod.class);
        String name = method.value();
        return "".equals(name) ? uncapitalizeFirst(bean.getClass().getSimpleName()) : name;
    }

    private String getDirectiveName(Object bean) {
        FMDirective directive = bean.getClass().getAnnotation(FMDirective.class);
        String name = directive.value();
        return "".equals(name) ? uncapitalizeFirst(bean.getClass().getSimpleName()) : name;
    }

    
    private String uncapitalizeFirst(String str) {
        char first = str.charAt(0);
        if (first >= 'A' && first <= 'Z') {
            char[] chars = str.toCharArray();
            chars[0] += 32;
            return new String(chars);
        }
        return str;
    }
}

