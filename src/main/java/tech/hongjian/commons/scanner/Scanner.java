package tech.hongjian.commons.scanner;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiahongjian 
 * @time   2019-03-21 14:20:33
 *
 */
@Data
public class Scanner {
	private String packageName;
	private boolean recursive;
	private Set<Class<?>> parents = new HashSet<>();
	private Set<Class<? extends Annotation>> annotations = new HashSet<>();
	
	public static ScannerBuilder builder() {
		return new ScannerBuilder();
	}
	
	public static class ScannerBuilder {
		private Scanner scanner;
		
		public ScannerBuilder() {
			scanner = new Scanner();
		}
		
		public ScannerBuilder packageName(String packageName) {
			if (packageName != null)
				scanner.setPackageName(packageName);
			return this;
		}
		
		public ScannerBuilder parents(Class<?> parent) {
			if (parent != null)
				scanner.getParents().add(parent);
			return this;
		}
		
		public ScannerBuilder parents(Set<Class<?>> parents) {
			scanner.getParents().addAll(parents);
			return this;
		}
		
		public ScannerBuilder annotations(Class<? extends Annotation> annotation) {
			if (annotation != null)
				scanner.getAnnotations().add(annotation);
			return this;
		}
		
		public ScannerBuilder annotations(Set<Class<? extends Annotation>> annotations) {
			scanner.getAnnotations().addAll(annotations);
			return this;
		}
		
		public ScannerBuilder recursive(boolean recursive) {
			scanner.setRecursive(recursive);
			return this;
		}
		
		public Scanner build() {
			return scanner;
		}
	}
}
