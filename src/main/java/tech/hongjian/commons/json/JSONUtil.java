package tech.hongjian.commons.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JSON工具类（json库使用的是jackson）
 * 
 * 
 * @author xiahongjian 
 * @time   2018-05-25 16:30:51
 *
 */
public class JSONUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtil.class);
	private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
	
	static {
		DEFAULT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	
	
	private JSONUtil() {}
	
	public static <T> String toJSON(T obj) {
		try {
			return DEFAULT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			LOGGER.warn("Failed to serilize object to JSON.", e);
		}
		return "";
	}
	
	public static <T> T toBean(String json, Class<T> clazz) {
		try {
			return DEFAULT_MAPPER.readValue(json, clazz);
		} catch (IOException e) {
			LOGGER.warn("Failed to parse JSON to object, JSON: {}", json, e);
		}
		return null;
	}
	
	public static <T> List<T> toList(String json, Class<T> clazz) {
		JavaType type = DEFAULT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
		try {
			return DEFAULT_MAPPER.readValue(json, type);
		} catch (IOException e) {
			LOGGER.warn("Failed to parse JSON to List object, JSON: {}", json, e);
		}
		return null;
	}
	
	
	public static <T> Map<String, T> toMap(String json, Class<T> clazz) {
		JavaType type = DEFAULT_MAPPER.getTypeFactory().constructMapLikeType(HashMap.class, String.class, clazz);
		try {
			return DEFAULT_MAPPER.readValue(json, type);
		} catch (IOException e) {
			LOGGER.warn("Failed to parse JSON to Map object, JSON: {}", json, e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("1", "a");
		map.put("2", "b");
		
		String json = toJSON(map);
		System.out.println(json);
		
		Map<String, String> m = toMap(json, String.class);
		m.forEach((key, value) -> {
			System.out.println("key: " + key + ", value: " + value);
		});
		
		User user = new User("tom", new Date());
		String userJson = toJSON(user);
		System.out.println(userJson);
		
		User u = toBean(userJson, User.class);
		System.out.println(u.getName());
		
		List<User> users = Arrays.asList(new User("a", new Date()), new User("2", new Date()));
		String usersJson = toJSON(users);
		System.out.println(userJson);
		List<User> list = toList(usersJson, User.class);
		list.forEach(e -> {
			System.out.println(e.getName());
		});
	}
	
	public static class User {
		private String name;
		private Date date;
		
		public User() {}
		public User(String name, Date date) {
			this.name = name;
			this.date = date;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
	}
}
