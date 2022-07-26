package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.EventBus;
import com.albertsons.specright.service.HttpClientService;
import com.albertsons.specright.service.KafkaService;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightEvent;

import java.net.URI;

public abstract class SpecrightComponent implements EventBus.Subscriber {

    public static final String SCANNER = "scanner";
    public static final String TOKEN = "token";
    public static final String SKIP = "skip";

    protected HttpClientService httpClientService() {
        return Specright.getInstance().getHttpClientService();
    }

    protected KafkaService kafkaService() {
        return Specright.getInstance().getKafkaService();
    }

    protected void handleException(Event event, Exception exception) {
        eventBuilder(SpecrightEvent.Exception_HANDLE_EVENT, event)
                .setPayload(exception)
                .create();
    }

    protected Event.Builder eventBuilder(SpecrightEvent specrightEvent, Event event) {
        Event.Builder builder = Event.builder(URI.create(specrightEvent.getUri()), event);
        if (event.getParameter(SCANNER) != null) {
            builder.addParameter(SCANNER, event.getParameter(SCANNER));
        }

        return builder;
    }

    public abstract SpecrightEvent[] listenTo();
}
