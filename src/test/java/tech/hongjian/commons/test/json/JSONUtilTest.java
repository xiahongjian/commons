package tech.hongjian.commons.test.json;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tech.hongjian.commons.json.JSONUtil;


/**
 * @author xiahongjian
 * @time 2019-08-27 20:53:34
 */
public class JSONUtilTest {

    @Test
    public void test() throws ParseException {
        Map<String, String> map = new HashMap<>();
        map.put("1", "a");
        map.put("2", "b");

        String json = JSONUtil.toJSON(map);
        String source = "{\"1\":\"a\",\"2\":\"b\"}";
        assertEquals(source, json);

        Map<String, String> m = JSONUtil.toMap(source, String.class);
        m.forEach((key, value) -> {
            if ("1".equals(key)) {
                assertEquals("a", value);
            }
            if ("2".equals(key)) {
                assertEquals("b", value);
            }
        });

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss").parse("2019-08-27 21:17:00");
        User user = new User("tom", date);
        String userJson = JSONUtil.toJSON(user);
        assertEquals("{\"name\":\"tom\",\"date\":\"2019-08-27 21:17:00\"}", userJson);


        User u = JSONUtil.toBean(userJson, User.class);
        assertEquals(user, u);

        List<User> users = Arrays.asList(new User("a", date), new User("b", date));
        String sourceUsersJson =
                "[{\"name\":\"a\",\"date\":\"2019-08-27 21:17:00\"},{\"name\":\"b\",\"date\":\"2019-08-27 21:17:00\"}]";
        String usersJson = JSONUtil.toJSON(users);
        assertEquals(sourceUsersJson, usersJson);

        List<User> list = JSONUtil.toList(sourceUsersJson, User.class);
        assertEquals(users, list);
    }

    public static class User {
        private String name;
        private Date date;

        public User() {}

        public User(String name, Date date) {
            this.name = name;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            User other = (User) obj;
            if (date == null) {
                if (other.date != null)
                    return false;
            } else if (!date.equals(other.date))
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }
    
    public static void main(String[] args) {
        String sourceUsersJson = "{\"name\":\"tom\",\"date\":\"2019-08-27\"}";
        User user = JSONUtil.toBean(sourceUsersJson, User.class);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.getDate()));
    }
}
