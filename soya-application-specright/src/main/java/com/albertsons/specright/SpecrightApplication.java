package com.albertsons.specright;

import com.albertsons.specright.component.SpecrightComponent;
import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.EventBus;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
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
            for(SpecrightEvent evt : component.listenTo()) {
                EventBus.getInstance().addSubscriber(evt.getUri(), "test", component);
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Event.builder(URI.create(SpecrightEvent.HEARTBEAT_EVENT.getUri()), "heartbeat-" + ++sequence).create();
            }
        }, heartbeatDelay(), heartbeatPeriod());
    }
}
