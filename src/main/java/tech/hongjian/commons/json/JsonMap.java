package tech.hongjian.commons.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 用于访问json深层次某些属性,使用json框架为fastjson
 * 
 * @author xiahongjian
 * @time 2018-05-28 10:32:39
 *
 */
public class JsonMap extends HashMap<String, Object> {
	private static final long serialVersionUID = -9223075738689311763L;
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMap.class);
	private static final Pattern LIST_PATTERN = Pattern.compile("(\\w+)\\[(\\d+)\\]$");

	public static JsonMap newInstance(String json) {
		try {
			return JSONObject.parseObject(json, JsonMap.class);
		} catch (Exception e) {
			LOGGER.info("Failed to parse json string.", e);
			return null;
		}
	}

	/**
	 * 根据所给的key获取属性
	 * 
	 * <pre>
	 * Example:
	 * 	String json = "{\"test\":{\"num\":\"8983431\"}, \"user\":{\"id\":123, \"username\":\"tom\", \"emails\":[\"email1\", \"email2\"]}}"
	 *  JsonMap jsonMap = JsonMap.newInstance(json);
	 *  Integer num = jsonMap.get("test.num", int.class); // or num = jsonMap.get("test.num", Integer.class);
	 *  User user = jsonMap.get("user");
	 *  String email = jsonMap.get("user.emails[0]");
	 * </pre>
	 * 
	 * @param key
	 *            获取属性使用的key(类似于js中属性的访问，但是属性只能用'.'访问，不能使用'[propName]', 数组元素需使用下标访问)
	 * @param clazz
	 *            返回数据类型的class对象
	 * @return 对应key的属性值
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		if (key == null)
			return null;

		key = key.replaceAll("\\.+", ".");
		String keys[] = key.split("\\.");

		Queue<String> queue = new LinkedList<>();
		queue.addAll(Arrays.asList(keys));

		Object tmp = this;
		Matcher m;
		while (!queue.isEmpty() && !tmp.getClass().isPrimitive()) {
			key = queue.poll();
			m = LIST_PATTERN.matcher(key);
			// 如果是数组访问表达式(如arr[2])
			if (m.find()) {

				String listName = m.group(1);
				// 获取到数组
				if (tmp instanceof JSONObject) {
					tmp = ((JSONObject) tmp).get(listName);
				} else {
					tmp = ((JsonMap) tmp).get(listName);
				}

				if (!(tmp instanceof List)) { // 如果不是list，返回null
					return null;
				}

				int index = Integer.valueOf(m.group(2));
				List<?> list = (List<?>) tmp;
				tmp = index > list.size() - 1 ? null : list.get(index);
			} else { // 不是数组访问表达式
				if (tmp instanceof JSONObject) {
					tmp = ((JSONObject) tmp).get(key);
				} else if (tmp instanceof JsonMap) {
					tmp = ((JsonMap) tmp).get(key);
				}
			}

			if (tmp == null)
				return null;
		}

		// 还有为访问的的下层属性则返回null
		if (!queue.isEmpty())
			return null;

		Class<?> tmpClass = tmp.getClass();
		if (tmpClass.equals(clazz))
			return (T) tmp;

		if (clazz.isPrimitive() || isPrimitive(clazz)) {
			try {
				if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
					tmp = Integer.valueOf(tmp.toString());
				} else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
					tmp = Boolean.valueOf(tmp.toString());
				} else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
					tmp = Long.valueOf(tmp.toString());
				} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
					tmp = Double.valueOf(tmp.toString());
				} else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
					tmp = Float.valueOf(tmp.toString());
				} else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
					tmp = Short.valueOf(tmp.toString());
				} else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
					tmp = Byte.valueOf(tmp.toString());
				} else {
					tmp = null;
				}
			} catch (Exception e) {
				tmp = null;
			}
			return (T) tmp;
		}

		if (clazz.equals(String.class)) {
			tmp = tmp.toString();
			return (T) tmp;
		}

		if (tmp instanceof JSONArray) {
			return ((JSONArray) tmp).toJavaObject(clazz);
		}

		if (tmp instanceof JSONObject) {
			return ((JSONObject) tmp).toJavaObject(clazz);
		}

		return null;
	}

	private boolean isPrimitive(Class<?> clazz) {
		try {
			return ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}
}
