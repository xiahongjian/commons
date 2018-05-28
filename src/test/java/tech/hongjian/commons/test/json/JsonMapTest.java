package tech.hongjian.commons.test.json;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

import tech.hongjian.commons.json.JsonMap;

/**
 * 
 * @author xiahongjian
 * @time 2018-05-28 12:57:08
 *
 */
public class JsonMapTest {
	@Test
	public void jsonMap() {
		JsonMap json = JsonMap.newInstance(
				"{\"test\":{\"pp\":\"8983431\"}, \"user\":{\"id\":123, \"username\":\"linus\", \"emails\":[\"email1\", \"email2\"]}}");

		System.out.println(json.get("test.pp", int.class));
		System.out.println(json.get("user", User.class));
		System.out.println(json.get("user.emails[0]", String.class));
	}

//	@Test
//	public void testReg() {
//		Pattern p = Pattern.compile("(\\w+)\\[(\\d+)\\]$");
//
//		Matcher a = p.matcher("nihao[4]");
//		a.find();
//		System.out.println(a.group(2));
//	}
}

class User {
	private Long id;
	private String username;
	private String[] emails;

	public String toString() {
		return JSON.toJSONString(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getEmails() {
		return emails;
	}

	public void setEmails(String[] emails) {
		this.emails = emails;
	}
}
