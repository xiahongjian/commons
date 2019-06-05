package tech.hongjian.commons.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiahongjian 
 * @time   2019-03-21 14:41:46
 *
 */
public class ClassDefine {
	private static final Map<Class<?>, ClassDefine> POOL = new ConcurrentHashMap<>();
	
	private final Class<?> clazz;
	
	private ClassDefine(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	private static ClassDefine of(Class<?> clazz) {
		ClassDefine classDefine = POOL.get(clazz);
		if (classDefine == null) {
			classDefine = new ClassDefine(clazz);
			ClassDefine old = POOL.putIfAbsent(clazz, classDefine);
			if (old != null) {
				classDefine = old;
			}
		}
		return classDefine;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Class<T> getType() {
		return (Class<T>) clazz;
	}
	
	public String getName() {
		return clazz.getName();
	}
	
	public String getSimpleName() {
		return clazz.getSimpleName();
	}
	
	public ClassDefine getSuperClazz() {
		Class<?> superClazz = clazz.getSuperclass();
		return superClazz == null ? null : ClassDefine.of(superClazz);
	}
	
	public List<ClassDefine> getInterfaces() {
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces.length == 0) {
			return Collections.emptyList();
		}
		List<ClassDefine> results = new ArrayList<>(interfaces.length);
		for (Class<?> inter : interfaces) {
			results.add(ClassDefine.of(inter));
		}
		return results;
	}
	
	public Annotation[] getAnnotations() {
		return clazz.getAnnotations();
	}
	
	public <T extends Annotation> T getAnnotation(Class<T> annotation) {
		return clazz.getAnnotation(annotation);
	}
	
	public Field[] getDeclaredField() {
		return clazz.getDeclaredFields();
	}
	
	public int getModifiers() {
		return clazz.getModifiers();
	}
	
	public boolean isInterface() {
		return Modifier.isInterface(getModifiers());
	}
	
	public boolean isAbstract() {
		return Modifier.isAbstract(getModifiers());
	}
	
	public boolean isStatic() {
		return Modifier.isStatic(getModifiers());
	}
	
	public boolean isPrivate() {
		return Modifier.isPrivate(getModifiers());
	}
	
	public boolean isPublic() {
		return Modifier.isPublic(getModifiers());
	}
	
	public boolean isProtected() {
		return Modifier.isProtected(getModifiers());
	}
}
