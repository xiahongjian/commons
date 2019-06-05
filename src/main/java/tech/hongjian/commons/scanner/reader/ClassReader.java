package tech.hongjian.commons.scanner.reader;


import tech.hongjian.commons.scanner.ClassInfo;
import tech.hongjian.commons.scanner.Scanner;

import java.util.Set;

/**
 * @author xiahongjian 
 * @time   2019-03-21 15:10:12
 *
 */
public interface ClassReader {
	Set<ClassInfo> readClasses(Scanner scanner);
}
