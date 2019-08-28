package tech.hongjian.commons.json;
/** 
 * @author xiahongjian
 * @time   2019-08-28 23:45:04
 */

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YAMLUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(YAMLUtil.class);
    
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
   
    private YAMLUtil() {}
    
    public static <T> T toBean(String source, Class<T> clazz) {
        try {
            return MAPPER.readValue(source, clazz);
        } catch (IOException e) {
            LOGGER.warn("Failed to parse the YAML string to an object, string: {}", source);
        }
        return null;
    }
    
    public static <T> List<T> toList(String source, Class<?> clazz) {
        JavaType type = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return MAPPER.readValue(source, type);
        } catch (IOException e) {
            LOGGER.warn("Failed to parse the YAML string to a list, string: {}", source);
        }
        return null;
    }
    
    public static <T> String toYAML(T value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to serilize the object to YAML.");
        }
        return null;
    }
}
