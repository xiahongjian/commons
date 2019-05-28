package tech.hongjian.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tech.hongjian.commons.model.KVPair;

/**
 * 枚举工具类
 * 
 * @author xiahongjian 
 * @time   2018-04-11 15:20:28
 *
 */
public class EnumUtil {
	/**
     * 根据指定枚举类Class对象和字符串，返回对应的枚举对象
     * @param enumType  枚举类的Class对象
     * @param name      枚举name的字符串
     * @param <E>       返回的枚举对象的类型
     * @return  返回字符串对应枚举对象，当字符串为空或对应的枚举对象不存在的时候，返回null
     */
    public static <E extends Enum<E>> E valueOf(Class<E> enumType, String name) {
        return valueOf(enumType, name, null);
    }

    /**
     * 根据指定枚举类Class对象和字符串，返回对应的枚举对象
     * @param enumType      枚举类的Class对象
     * @param name          枚举name的字符串
     * @param defaultValue  默认返回值
     * @param <E>           返回的枚举对象的类型
     * @return  返回字符串对应枚举对象，当字符串为空或对应的枚举对象不存在的时候，返回默认返回值
     */
    public static <E extends Enum<E>> E valueOf(Class<E> enumType, String name, E defaultValue) {
        if (name == null)
            return defaultValue;
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException  e) {
            return defaultValue;
        }
    }

    /**
     * 根据指定的枚举类Class对象，返回包含对应枚举的所有枚举对象的list
     * @param enumType  枚举类的Class对象
     * @param <E>       枚举类型
     * @return          包含对应枚举的多有枚举对象的list
     */
    public static <E extends Enum<E>> List<E> getEnumList(Class<E> enumType) {
        return new ArrayList<>(Arrays.asList(enumType.getEnumConstants()));
    }

    public static <E extends Enum<E>> List<KVPair> getCaptionList(Class<E> enumType) {
        // 单枚举类没有实现HasCaption接口时，直接返回空list
        if (!implementsHasCaption(enumType))
            return Collections.emptyList();
        E[] constants = enumType.getEnumConstants();
        List<KVPair> list = new ArrayList<>(constants.length);
        for (E c : constants) {
            list.add(new KVPair(c.name(), c instanceof HasCaption ? ((HasCaption) c).getCaption() : c.name()));
        }
        return list;
    }

    public static <E extends Enum<E>> Map<String, E> getEnumMap(Class<E> enumType) {
        return Stream.of(enumType.getEnumConstants()).collect(Collectors.toMap(e -> e.name(), e -> e));
    }

    public static <E extends Enum<E>> Map<String, String> getNameCaptionMap(Class<E> enumType) {
        // 单枚举类没有实现HasCaption接口时，直接返回空list
        if (!implementsHasCaption(enumType))
            return new HashMap<>(0);
        E[] constants = enumType.getEnumConstants();
        Map<String, String> map = new HashMap<>(constants.length);
        for (E c : constants) {
            map.put(c.name(), c instanceof HasCaption ? ((HasCaption) c).getCaption() : c.name());
        }
        return map;
    }


    private static <E> boolean implementsHasCaption(Class<E> enumType) {
        return HasCaption.class.isAssignableFrom(enumType);
    }
}
