package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightEvent;
import org.springframework.stereotype.Component;

import java.net.URI;
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
            Event.builder(URI.create(SpecrightEvent.KAFKA_PRODUCE_EVENT.getUri()), event)
                    .addParameter(SCANNER, event.getParameter(SCANNER))
                    .setPayload(msg)
                    .create();
        }
    }

    private String invoke() {
        return UUID.randomUUID().toString();
    }

    @Override
    public SpecrightEvent[] listenTo() {
        return new SpecrightEvent[] {SpecrightEvent.API_INVOKE_EVENT};
    }
}
