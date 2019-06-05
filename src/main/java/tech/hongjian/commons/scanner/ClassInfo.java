package tech.hongjian.commons.scanner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiahongjian 
 * @time   2019-03-21 14:33:39
 *
 */
@Slf4j
@Getter
public class ClassInfo {
	private String className;
	private Class<?> clazz;
	
	public ClassInfo(Class<?> clazz) {
		this.className = clazz.getName();
		this.clazz = clazz;
	}
	
	public ClassInfo(String className, Class<?> clazz) {
		this.className = className;
		this.clazz = clazz;
	}
	
	public static ClassInfo of(Class<?> clazz) {
		return new ClassInfo(clazz);
	}
	
	public Object newInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Failed to new instance for class: {}", className, e);
		}
		return null;
	}
}
