package tech.hongjian.commons.scanner.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tech.hongjian.commons.scanner.ClassInfo;
import tech.hongjian.commons.scanner.Scanner;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author xiahongjian
 * @time 2019-03-22 09:19:37
 *
 */
@Slf4j
public final class DynamicContext {
	private static final ClassReader CLASSPATH_READER = new ClassPathClassReader();
	private static final ClassReader JAR_READER = new JarClassReader();
	private static final String JAR_SUFFIX = ".jar";

	private static boolean isJarContext = false;

	public static void init(Class<?> clazz) {
		String rs = clazz.getResource("").toString();
		if (rs.contains(JAR_SUFFIX)) {
			isJarContext = true;
		}
	}

	public static Stream<ClassInfo> recursionFindClasses(String packageName) {
		Scanner scanner = Scanner.builder().packageName(packageName).recursive(true).build();
		Set<ClassInfo> classInfos = getClassReader(packageName).readClasses(scanner);
		return classInfos.stream();
	}
	
	public static Stream<ClassInfo> recursionFindClasses(String packageName, Class<?> parent,
			Class<? extends Annotation> annotation) {
		Scanner scanner = Scanner.builder().packageName(packageName).recursive(true).parents(parent)
				.annotations(annotation).build();
		return recursionFindClasses(scanner).stream();
	}

	public static Stream<ClassInfo> recursionFindClasses(String packageName, Set<Class<?>> parents,
			Set<Class<? extends Annotation>> annotations) {
		Set<ClassInfo> classInfos = new HashSet<>();
		Scanner scanner = Scanner.builder().packageName(packageName).recursive(true).parents(parents)
				.annotations(annotations).build();
		classInfos.addAll(recursionFindClasses(scanner));
		return classInfos.stream();
	}
	

	public static Stream<ClassInfo> recursionFindClasses(Set<String> packageNames, Set<Class<?>> parents,
			Set<Class<? extends Annotation>> annotations) {
		Set<ClassInfo> classInfos = new HashSet<>();
		packageNames.forEach(packageName -> {
			Scanner scanner = Scanner.builder().packageName(packageName).recursive(true).parents(parents)
					.annotations(annotations).build();
			classInfos.addAll(recursionFindClasses(scanner));
		});
		return classInfos.stream();
	}
	
	private static Set<ClassInfo> recursionFindClasses(Scanner scanner) {
		return getClassReader(scanner.getPackageName()).readClasses(scanner);
	}

	public static ClassReader getClassReader(String packageName) {
		if (isJarPackage(packageName)) {
			return JAR_READER;
		}
		return CLASSPATH_READER;
	}

	public static boolean isJarPackage(String packageName) {
		if (StringUtils.isBlank(packageName))
			return false;
		packageName = packageName.replace('.', '/');
		Enumeration<URL> urls;
		try {
			urls = DynamicContext.class.getClassLoader().getResources(packageName);
			if (urls.hasMoreElements()) {
				String url = urls.nextElement().toString();
				return url.indexOf(".jar!") != -1 || url.indexOf(".zip!") != -1;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public static boolean isJarContext() {
		return isJarContext;
	}
}
