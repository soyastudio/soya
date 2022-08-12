package com.albertsons.specright.component;

import com.albertsons.specright.service.Configuration;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightException;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Random;
import java.util.logging.Logger;

@Component
@Subscriber.ListenTo(Specright.EVENT_HEARTBEAT)
public class ScanTrigger extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(ScanTrigger.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        logger.info("================================== heartbeat: " + specright.getSequence() + " ==================================");
        String token = specright.fetchToken();

        for (String scanner : specright.scanners()) {
            try {
                Thread.sleep(new Random().nextInt(10000));

            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            scan(scanner, token, event);

        }
    }

    protected void scan(String scanner, String token, Event event) throws SpecrightException {
        if (debug()) {
            logger.info("Scan for: " + scanner);
        }

        int refreshFrequency = Configuration.get(Configuration.REFRESH_FREQUENCY) == null ? 7 : Integer.parseInt(Configuration.get(Configuration.REFRESH_FREQUENCY));
        boolean refresh = specright.getSequence() % refreshFrequency == 1;

        String jobId = specright.scan(scanner, token);
        if (debug()) {
            logger.info("Scan job created for '" + scanner + "' with id: " + jobId);
        }

        Event.builder(URI.create(Specright.EVENT_JOB_TRACKING), event)
                .addParameter(SCANNER, scanner)
                .addParameter(REFRESH, "" + refresh)
                .addParameter(JOB_ID, jobId)
                .addParameter(TOKEN, token)
                .create();
    }
}
