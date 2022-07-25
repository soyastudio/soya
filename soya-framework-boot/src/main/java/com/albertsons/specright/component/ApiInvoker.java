package com.albertsons.specright.component;

import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;

import java.net.URI;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Component("api-invoker")
public class ApiInvoker extends SpecrightComponent {
    static final Logger logger = Logger.getLogger(ApiInvoker.class.getName());

    @Override
    public void onEvent(Event event) {
        logger.info(event.toURI());

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(300l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String msg = invoke();
            Event.builder(URI.create("specright://kafka-producer"), event)
                    .addParameter(SCANNER, event.getParameter(SCANNER))
                    .setPayload(msg)
                    .create();
        }
    }

    private String invoke() {
        return UUID.randomUUID().toString();
    }
}
