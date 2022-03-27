package soya.framework.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"soya.framework.springboot"})
public class SoyaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoyaApplication.class, args);
    }

}
