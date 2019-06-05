package tech.hongjian.commons.scanner;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiahongjian 
 * @time   2019-03-22 16:47:52
 *
 */
@Slf4j
public class BeanContainer {
	private static final Map<Class<?>, Object> BEANS = new HashMap<>(); 
	
	@SuppressWarnings("unchecked")
	public static <T> T add(Class<T> clazz, T obj) {
		if (BEANS.containsKey(clazz))
			return (T) BEANS.get(clazz);
		BEANS.put(clazz, obj);
		return obj;
	}
	
	public static <T> T add(Class<T> clazz) {
		try {
			return add(clazz, clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Failed to create bean for class: {}.", clazz.getName(), e);
		}
		return null;
	}
	
	public static void add(Collection<Class<?>> classes) {
		classes.forEach(c -> add(c));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz) {
		return (T) BEANS.get(clazz);
	}
}
