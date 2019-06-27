package tech.hongjian.commons.test.freemarker;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import freemarker.template.TemplateException;
import tech.hongjian.commons.freemarker.FreeMarkerUtil;

/** 
 * @author xiahongjian
 * @time   2019-06-27 19:10:02
 */
public class TestFreeMarkerUtil {

    @Test
    public void testStringTemplate() throws TemplateException, IOException {
        String source = "Hi, ${name}.";
        Map<String, Object> model = new HashMap<>();
        model.put("name", "Tom");
        String templateName = "string_template";
        assertEquals("Hi, Tom.", FreeMarkerUtil.process(templateName, source, model).toString());
        assertEquals("Hi, Tom.", FreeMarkerUtil.process(templateName, null, model).toString());
        assertEquals("Hi, Tom.", FreeMarkerUtil.process(source, model).toString());
    }
}
