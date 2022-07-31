package com.albertsons.specright;

import com.albertsons.specright.component.SpecrightComponent;
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
        PostmanEnvironment environment = GSON.fromJson(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("specright_postman_environment.json")),
                PostmanEnvironment.class);

        PostmanCollection collection = PostmanCollection.fromInputStream(getClass().getClassLoader().getResourceAsStream("specright_postman_collection.json"));

        configure(environment, collection);
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
                Event e = Event.builder(URI.create(EVENT_HEARTBEAT), "heartbeat-" + ++sequence).create();
            }
        }, heartbeatDelay, heartbeatPeriod);
    }


}
