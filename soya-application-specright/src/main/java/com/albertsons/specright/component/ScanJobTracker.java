package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.logging.Logger;

@Component
@Subscriber.ListenTo(Specright.EVENT_JOB_TRACKING)
public class ScanJobTracker extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(ScanJobTracker.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        if(debug()) {
            logger.info("Tracking scan result for: " + event.getParameter(SCANNER) + "; id: " + event.getParameter(JOB_ID));
        }

        try {
            Thread.sleep(5000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] results = specright.jobDetails(event.getParameter(JOB_ID), event.getParameter(TOKEN));
        if(debug()) {
            logger.info("Scan result size of " + event.getParameter(SCANNER) + ": " + results.length);
        }

        Event.builder(URI.create(Specright.EVENT_RESULT_EXPORT), event)
                .addParameter(SCANNER, event.getParameter(SCANNER))
                .setPayload(results)
                .create();

    }

}
