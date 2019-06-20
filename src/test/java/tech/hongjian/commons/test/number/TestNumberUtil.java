package tech.hongjian.commons.test.number;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static tech.hongjian.commons.number.NumberUtil.toTraditionalNumber;

public class TestNumberUtil {
    @Test
    public void test() {
        assertEquals("零", toTraditionalNumber(0));
        assertEquals("壹拾", toTraditionalNumber(10));
        assertEquals("玖拾伍", toTraditionalNumber(95));
        assertEquals("贰佰壹拾", toTraditionalNumber(210));
        assertEquals("贰佰壹拾叁", toTraditionalNumber(213));
        assertEquals("叁仟零贰", toTraditionalNumber(3002));
        assertEquals("叁仟零肆拾", toTraditionalNumber(3040));
        assertEquals("叁仟零肆拾捌", toTraditionalNumber(3048));

        assertEquals("伍万零贰佰零叁", toTraditionalNumber(5_0203));
        assertEquals("贰佰壹拾万零贰佰壹拾", toTraditionalNumber(210_0210));
        assertEquals("叁仟零肆拾万", toTraditionalNumber(3040_0000));
        assertEquals("叁仟万零捌", toTraditionalNumber(3000_0008));

        assertEquals("叁亿零捌", toTraditionalNumber(3_0000_0008L));
        assertEquals("伍佰亿", toTraditionalNumber(500_0000_0000L));
        assertEquals("伍佰亿零贰拾万零玖", toTraditionalNumber(500_0020_0009L));
        assertEquals("叁仟零玖亿零肆", toTraditionalNumber(3009_0000_0004L));
    }
}
