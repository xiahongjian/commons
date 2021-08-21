package tech.hongjian.commons.refect;


import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiahongjian on 2021/8/21.
 */
public abstract class LambdaUtils {

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 lambda 表达式, 该方法只是调用了 {@link SerializedLambda#resolve(SFunction)} )} 中的方法，在此基础上加了缓存。
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     * @see SerializedLambda#resolve(SFunction)
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(FUNC_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = SerializedLambda.resolve(func);
                    FUNC_CACHE.put(canonicalName, new WeakReference<>(lambda));
                    return lambda;
                });
    }
}
