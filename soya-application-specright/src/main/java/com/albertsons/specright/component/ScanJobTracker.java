package com.albertsons.specright.component;

import com.albertsons.specright.service.Event;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.Subscriber;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.logging.Logger;

@Component
@Subscriber.ListenTo(Specright.EVENT_JOB_TRACKING)
public class ScanJobTracker extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(ScanJobTracker.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        String scanner = event.getParameter(SCANNER);
        String refresh = event.getParameter(REFRESH);
        String jobId = event.getParameter(JOB_ID);

        try {
            Thread.sleep(15000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (debug()) {
            logger.info("Tracking scan result for: " + scanner + "; id: " + jobId);
        }

        byte[] results = specright.jobDetails(event.getParameter(JOB_ID), event.getParameter(TOKEN));
        results = specright.csvFilter(scanner, results);

        if(refresh != null && "false".equalsIgnoreCase(refresh)) {
            results = specright.filterByLastUpdated(scanner, results);
        }

        if(results.length > 0) {
            results = specright.gzip(results);

            if (debug()) {
                logger.info("Scan result size of " + scanner + ": " + results.length);
            }

            Event.builder(URI.create(Specright.EVENT_RESULT_EXPORT), event)
                    .addParameter(SCANNER, event.getParameter(SCANNER))
                    .setPayload(results)
                    .create();
        } else {
            if (debug()) {
                logger.info("No new changed data found for: " + scanner);
            }
        }

    }

}
