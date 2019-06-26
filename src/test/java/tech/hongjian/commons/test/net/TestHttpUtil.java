package tech.hongjian.commons.test.net;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import tech.hongjian.commons.net.RequestBuilder;

/** 
 * @author xiahongjian
 * @time   2019-06-26 22:37:23
 */
public class TestHttpUtil {
    
    @Test
    public void testRequestBuilder() throws Exception {
        assertNotNull(new RequestBuilder("www.baidu.com").charset("UTF-8").build().get());
        assertNotNull(new RequestBuilder("https://www.baidu.com").charset("UTF-8").build().get());
    }

}
