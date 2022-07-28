package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

@Component
@Subscriber.ListenTo(Specright.EVENT_API_INVOKE)
public class ApiInvoker extends SpecrightComponent {
    @Override
    protected void process(Event event) throws Exception {

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(300l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String msg = invoke();
            Event.builder(URI.create(Specright.EVENT_KAFKA_PRODUCE), event)
                    .addParameter(SCANNER, event.getParameter(SCANNER))
                    .setPayload(msg)
                    .create();
        }
    }

    private String invoke() {
        return UUID.randomUUID().toString();
    }
}
