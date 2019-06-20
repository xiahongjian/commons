package tech.hongjian.commons.number;

import java.util.Stack;

public class NumberUtil {
    public static final int TRADITION = 0;
    public static final int SIMPLE = 1;
    private static final String[][] CHINESE_NUMS = {
            {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"},
            {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"}
    };
    private static final String[][] BASE_UNITS = {
            {"", "拾", "佰", "仟"},
            {"", "十", "百", "千"}
    };
    private static final String[][] GROUP_UNITS = {
            { "", "万", "亿"},
            { "", "万", "亿"}
    };

    /**
     * 将阿拉伯数字转换成中文大写数字
     * @param n 需要转换的数，数的范围应该在[0, 9999_9999_9999]之间
     * @return 对应的中文大写数字
     */
    public static String toTraditionalNumber(long n) {
        return toChinese(n, TRADITION);
    }

    /**
     * 将阿拉伯数字转换成中文数字(非大写)
     * @param n 需要转换的数，数的范围应该在[0, 9999_9999_9999]之间
     * @return 对应的中文数字
     */
    public static String toSimpleNumber(long n) {
        return toChinese(n, SIMPLE);
    }


    public static String toChinese(long n, int mode) {
        if (mode > 1) {
            throw new IllegalArgumentException("The mode should be 0 or 1");
        }
        if (n > 9999_9999_9999L) {
            throw new IllegalArgumentException("The number is too large.");
        }

        if (n < 0) {
            throw new IllegalArgumentException("The number must be a positive number.");
        }

        Stack<Quadruple> groups = new Stack<>();
        for (int i = 0; n > 0; n /= 10000, i++) {
            int low4 = (int) (n % 10000);
            groups.push(handle4bit(low4, mode));
        }

        int size = groups.size();
        // 当只有一组时，n<10000，直接返回这组的字符串形式即可
        if (size == 1) {
            return groups.pop().name;
        }

        String s = "";
        int index = 0;
        boolean hasZero = false;
        while (!groups.empty()) {
            index++;
            Quadruple result = groups.pop();
            // 当这组值为0时，hasZero设置为true
            if (result.length == 0) {
                hasZero = true;
                continue;
            }

            if (hasZero || result.length < 4 && index != 1) {
                s += CHINESE_NUMS[mode][0];
                hasZero = false;
            }
            s += result.name + GROUP_UNITS[mode][size - index];
        }
        // 此时返回零
        if ("".equals(s)) {
            return CHINESE_NUMS[mode][0];
        }

        return s;
    }

    private static Quadruple handle4bit(int n, int mode) {

        Stack<Integer> bits = new Stack<>();
        for (int i = 0; n > 0; n /= 10, i++) {
            bits.push(n % 10);
        }
        boolean hasHigh = false;
        boolean hasZero = false;
        int index = 0;
        String s  = "";

        int size = bits.size();
        while (!bits.empty()) {
            index++;
            int bit = bits.pop();
            if (!hasHigh && bit == 0) {
                continue;
            }

            // 若当前位为0
            if (bit == 0) {
                hasZero = true;
                continue;
            }

            if (hasZero) {
                s += CHINESE_NUMS[mode][0];
                hasZero = false;
            }

            s += CHINESE_NUMS[mode][bit] + BASE_UNITS[mode][size - index];
            hasHigh = true;
        }
        if (size == 0) {
            s = CHINESE_NUMS[mode][0];
        }
        return new Quadruple(s, size);
    }


    static class Quadruple {
        String name;
        int length;
        public Quadruple(String name, int length) {
            this.name = name;
            this.length = length;
        }
    }
}
