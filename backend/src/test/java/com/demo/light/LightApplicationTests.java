package com.demo.light;



import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@SpringBootTest
@Slf4j
class LightApplicationTests {
@Test
    public  void test01() {
    String encoded = new BCryptPasswordEncoder().encode("123456");
    log.info(encoded);

}

    public class MyDate{

        private Date date;
    }
@Test
    public void test02(){



}
}
