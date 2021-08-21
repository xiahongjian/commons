package tech.hongjian.commons.refect;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的Function
 * Created by xiahongjian on 2021/8/21.
 */
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
