package soya.framework.springboot.configuration;

import com.albertsons.specright.service.HttpClientService;
import com.albertsons.specright.service.KafkaService;
import com.albertsons.specright.service.Specright;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import soya.framework.commons.eventbus.EventBus;
import soya.framework.commons.eventbus.Subscriber;

import java.io.InputStream;
import java.util.Timer;

@Configuration
@ComponentScan(basePackages = "com.albertsons.specright.component")
public class SpecrightConfiguration {
    private static final String SCHEMA = "specright://";

    @Autowired
    Environment environment;

    @Bean
    HttpClientService httpClientService() {
        return new HttpClientService();
    }

    @Bean
    KafkaService kafkaService() {
        return new KafkaService();
    }

    @Bean
    Specright scanners() {
        return new DefaultSpecright(getClass().getClassLoader().getResourceAsStream("specright.json"));
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBeansOfType(Subscriber.class).entrySet().forEach(e -> {
            EventBus.getInstance().addSubscriber(SCHEMA + e.getKey(), "test", e.getValue());
        });
    }

    static class DefaultSpecright extends Specright {

        protected DefaultSpecright(InputStream inputStream) {
            super(inputStream);
        }
    }
}
