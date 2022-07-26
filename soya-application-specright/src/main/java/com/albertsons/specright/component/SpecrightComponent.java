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
        Event.builder(URI.create("specright://exception-handler"), event)
                .setPayload(exception)
                .create();
    }

    public abstract SpecrightEvent[] listenTo();
}
