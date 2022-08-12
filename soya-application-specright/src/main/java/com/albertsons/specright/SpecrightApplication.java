package com.albertsons.specright;

import com.albertsons.specright.service.Configuration;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.EventBus;
import com.albertsons.specright.service.eventbus.Subscriber;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class SpecrightApplication extends Specright {

    public static void main(String[] args) {
        SpringApplication.run(SpecrightApplication.class, args);
    }

    @PostConstruct
    protected void configure() {
        String url = SpecrightApplication.class.getProtectionDomain().getCodeSource().getLocation().toString();

        PostmanEnvironment environment = GSON.fromJson(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("specright_postman_environment.json")),
                PostmanEnvironment.class);

        // ---------------- configuration from postman environment json.
        Properties properties = new Properties();
        environment.entrySet().forEach(e -> {
            properties.setProperty(e.getKey(), e.getValue());
        });
        new DefaultConfiguration(properties);

        PostmanCollection collection = PostmanCollection.fromInputStream(getClass().getClassLoader().getResourceAsStream("specright_postman_collection.json"));
        configure(collection);
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBeansOfType(Subscriber.class).entrySet().forEach(e -> {
            Subscriber component = e.getValue();
            Subscriber.ListenTo listenTo = component.getClass().getAnnotation(Subscriber.ListenTo.class);
            if (listenTo != null) {
                for (String evt : listenTo.value()) {
                    EventBus.subscribe(evt, component);
                }

            }
        });

        long heartbeatDelay = Configuration.get(Configuration.HEARTBEAT_DELAY) == null ? 10000l : Long.parseLong(Configuration.get(Configuration.HEARTBEAT_DELAY));
        long heartbeatPeriod = Configuration.get(Configuration.HEARTBEAT_PERIOD) == null ? 10000l : Long.parseLong(Configuration.get(Configuration.HEARTBEAT_PERIOD));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Event.builder(URI.create(EVENT_HEARTBEAT), ++sequence).create();
            }
        }, heartbeatDelay, heartbeatPeriod);
    }

    static class DefaultConfiguration extends Configuration {
        protected DefaultConfiguration(Properties properties) {
            super(properties);
        }
    }


}
