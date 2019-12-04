package soya.framework.dovetails.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"soya.framework.dovetails.application"})
public class DovetailsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DovetailsApplication.class, args);
    }
}
