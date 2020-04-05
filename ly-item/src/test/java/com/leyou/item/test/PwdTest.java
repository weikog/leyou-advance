package com.leyou.item.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PwdTest {

    //第一次加密：$2a$10$RgABw7j95OXVWItuDr9.kuttSkvKm55EU7rWg7PdovcEPUIoYJlWW
    //第二次加密：$2a$10$m2Lw9rp6ecquFbdzGu0Oku3ldCOLSYX2cI/E60eH3S0MAeq.b62tK
    //第三次加密：$2a$10$qWi6DtTZdfQltrE0jbn9EeyvRTn6pbV0OHvOANgtzua0zt6NKh2wm
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncoder = encoder.encode("123");
        System.out.println(pwdEncoder);
    }

}
