package com.albertsons.specright.component;

import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Subscriber.ListenTo(Specright.EVENT_JOB_TRACKING)
public class ScanJobTracker extends SpecrightComponent {

    @Override
    protected void process(Event event) throws Exception {
        byte[] results = Specright.getInstance().trackJob(event.getParameter(JOB_ID), event.getParameter(TOKEN));
        Event.builder(URI.create(Specright.EVENT_RESULT_EXPORT), event)
                .addParameter(SCANNER, event.getParameter(SCANNER))
                .setPayload(results)
                .create();

    }

}
