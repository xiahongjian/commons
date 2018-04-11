package tech.hongjian.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiahongjian 
 * @time   2018-04-11 15:09:12
 *
 */
public class ListUtil {
	/**
     * 根据传入的数组元素创建一个{@code ArrayList}的实例，不同于{@code Arrays.asList}的是，
     * 后者返回的是{@code List}实例为{@code Arrays}的内部类，不支持{@code add}和{@code remove}等操作
     * @param eles 用于填充list的元素，当其为null或长度为0时，返回一个空的list
     * @return 填充着传入元素的list
     */
    @SafeVarargs
	public static <T> List<T> of(T... eles) {
        if (eles == null || eles.length == 0)
            return new ArrayList<>(0);
        List<T> list = new ArrayList<>(eles.length);
        for (T e : eles)
            list.add(e);
        return list;
    }

    public static <T> T first(List<T> list) {
        if (list == null || list.isEmpty())
            return null;
        return list.iterator().next();
    }
}
