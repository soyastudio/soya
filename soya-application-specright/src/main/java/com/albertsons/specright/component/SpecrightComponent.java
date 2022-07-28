package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.Subscriber;
import com.albertsons.specright.service.HttpClientService;
import com.albertsons.specright.service.KafkaService;
import com.albertsons.specright.service.Specright;

import java.net.URI;

public abstract class SpecrightComponent implements Subscriber {

    public static final String SCANNER = "scanner";
    public static final String TOKEN = "token";
    public static final String SKIP = "skip";

    public void onEvent(Event event) {
        try {
            process(event);

        } catch (Exception e) {
            eventBuilder(Specright.EVENT_Exception_HANDLE, event)
                    .setPayload(e)
                    .create();
        }
    }

    protected abstract void process(Event event) throws Exception;

    protected HttpClientService httpClientService() {
        return Specright.getInstance().getHttpClientService();
    }

    protected KafkaService kafkaService() {
        return Specright.getInstance().getKafkaService();
    }

    protected Event.Builder eventBuilder(String uri, Event event) {
        Event.Builder builder = Event.builder(URI.create(uri), event);
        if (event.getParameter(SCANNER) != null) {
            builder.addParameter(SCANNER, event.getParameter(SCANNER));
        }

        return builder;
    }
}
