package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

public abstract class SpecrightComponent implements Subscriber {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String SCANNER = "scanner";
    public static final String TOKEN = "token";
    public static final String JOB_ID = "job-id";

    @Autowired
    protected Specright specright;

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

    protected Event.Builder eventBuilder(String uri, Event event) {
        Event.Builder builder = Event.builder(URI.create(uri), event);
        if (event.getParameter(SCANNER) != null) {
            builder.addParameter(SCANNER, event.getParameter(SCANNER));
        }

        return builder;
    }

    protected boolean debug() {
        return specright.debug();
    }


}
