package com.albertsons.specright;

import com.albertsons.specright.component.SpecrightComponent;
import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.EventBus;
import com.albertsons.specright.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Swagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class SpecrightApplication extends Specright {

    private int sequence;

    public static void main(String[] args) {
        SpringApplication.run(SpecrightApplication.class, args);
    }

    @PostConstruct
    protected void configure() {
        configure(getClass().getClassLoader().getResourceAsStream("specright.json"));
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBeansOfType(SpecrightComponent.class).entrySet().forEach(e -> {
            SpecrightComponent component = e.getValue();
            Subscriber.ListenTo listenTo = component.getClass().getAnnotation(Subscriber.ListenTo.class);
            if (listenTo != null) {
                for (String evt : listenTo.value()) {
                    EventBus.subscribe(evt, component);
                }

            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Event.builder(URI.create(EVENT_HEARTBEAT), "heartbeat-" + ++sequence).create();
            }
        }, heartbeatDelay(), heartbeatPeriod());
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


}
