package tech.hongjian.commons.freemarker;
/**
 * @author xiahongjian
 * @time 2019-06-27 18:33:16
 */

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerUtil {
    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_28);
    private static final Map<String, Template> TEMPATES = new ConcurrentHashMap<>();

    static {
        CFG.setDefaultEncoding("UTF-8");
        CFG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CFG.setDateFormat("yyyy-MM-dd");
        CFG.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 根据提供的模板字符串和数据模型渲染模板
     * 
     * @param name 模板名称，用于缓存{@link Template}对象，如果不需要缓存则传入null
     * @param source 模板字符串
     * @param model 数据模型
     * @return 渲染后的模板
     * @throws TemplateException
     * @throws IOException
     */
    public static Writer process(String name, String source, Map<String, Object> model)
            throws TemplateException, IOException {
        Writer out = new StringWriter();
        if (name != null && TEMPATES.containsKey(name)) {
            TEMPATES.get(name).process(model, out);
            return out;
        }
        if (name == null) {
            name = genTemlateName();
        }
        Template t = new Template(name, source, CFG);
        if (name != null) {
            TEMPATES.put(name, t);
        }
        t.process(model, out);
        return out;
    }

    public static Writer process(String source, Map<String, Object> model)
            throws TemplateException, IOException {
        return process(null, source, model);
    }

    private static String genTemlateName() {
        return "__template__" + System.currentTimeMillis();
    }
}
