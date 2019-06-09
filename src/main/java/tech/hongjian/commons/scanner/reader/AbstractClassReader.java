package tech.hongjian.commons.scanner.reader;

import lombok.extern.slf4j.Slf4j;
import tech.hongjian.commons.scanner.ClassInfo;
import tech.hongjian.commons.scanner.Scanner;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiahongjian
 * @time 2019-03-21 15:11:10
 */
@Slf4j
public class AbstractClassReader implements ClassReader {

    @Override
    public Set<ClassInfo> readClasses(Scanner scanner) {
        return filterClass(scanner.getPackageName(), scanner.getParents(),
				scanner.getAnnotations(),
                scanner.isRecursive());
    }

    public Set<ClassInfo> filterClass(String packageName, Set<Class<?>> parents,
                                      Set<Class<? extends Annotation>> annotations,
                                      boolean recursive) {
        final Set<ClassInfo> classInfos = new HashSet<>();
        String packageDir = packageName.replace(".", "/");

        Enumeration<URL> urls;
        try {
            urls = AbstractClassReader.class.getClassLoader().getResources(packageDir);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String filePath = new URI(url.getFile()).getPath();
                findClassByPackage(packageName, filePath, parents, annotations,
						recursive, classInfos);
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            log.error("No such class.", e);
        }

        return classInfos;
    }

    private Set<ClassInfo> findClassByPackage(String packageName, String packagePath,
											  Set<Class<?>> parents,
                                              Set<Class<? extends Annotation>> annotations, boolean recursive, Set<ClassInfo> classInfos)
            throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("The package [{}] is not found.", packageName);
        }
        File[] dirFiles = accept(dir, recursive);
        if (dirFiles == null || dirFiles.length == 0) {
            return classInfos;
        }

        for (File f : dirFiles) {
            if (f.isDirectory()) {
                findClassByPackage(packageName + "." + f.getName(), f.getAbsolutePath()
						, parents, annotations,
                        recursive, classInfos);
                continue;
            }

            String className = f.getName().substring(0, f.getName().length() - 6);
            Class<?> clazz = Class.forName(packageName + "." + className);
            determaines(clazz, parents, annotations, classInfos);
        }

        return classInfos;
    }

    private File[] accept(File file, boolean recursive) {
        return file.listFiles(f -> (recursive && f.isDirectory()) || (f.getName().endsWith(".class")));
    }

    protected boolean parentIsIn(Class<?> clazz, Set<Class<?>> parents) {
        Class<?> parent = clazz.getSuperclass();
        if (parent != null && parents.contains(parent)) {
            return true;
        }
        for (Class<?> inter : clazz.getInterfaces()) {
            if (parents.contains(inter)) {
                return true;
            }
        }
        return false;
    }

    protected boolean annotationIsIn(Class<?> clazz,
									 Set<Class<? extends Annotation>> annotations) {
        return annotations.stream().anyMatch(a -> clazz.getAnnotation(a) != null);
    }

    protected void determaines(Class<?> clazz, Set<Class<?>> parents, Set<Class<? extends Annotation>> annotations, Set<ClassInfo> classes) {
        if (parents == null && annotations == null) {
            classes.add(ClassInfo.of(clazz));
            return;
        }
        if (parents != null && parentIsIn(clazz, parents)) {
            classes.add(ClassInfo.of(clazz));
        }
        if (annotations != null && annotationIsIn(clazz, annotations)) {
            classes.add(ClassInfo.of(clazz));
        }
    }

}
