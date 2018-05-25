package tech.hongjian.commons.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:16:10
 *
 */
public class SetUtil {
	@SafeVarargs
	public static <T> Set<T> of(T... eles) {
        if (eles == null || eles.length == 0)
            return new HashSet<>(0);
        Set<T> set = new HashSet<>(eles.length);
        for (T ele : eles)
            set.add(ele);
        return set;
    }

    public static <T> T first(Set<T> set) {
        if (set == null || set.isEmpty())
            return null;
        return set.iterator().next();
    }
}
