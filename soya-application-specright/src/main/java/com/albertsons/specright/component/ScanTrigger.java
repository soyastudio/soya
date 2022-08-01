package com.albertsons.specright.component;

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
        Specright.Token token = specright.fetchToken();
        for (String scanner : specright.scanners()) {
            try {
                Thread.sleep(new Random().nextInt(10000));

            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            scan(scanner, token.getAccessToken(), event);

        }
    }

    protected void scan(String scanner, String token, Event event) throws SpecrightException {
        if(debug()) {
            logger.info("Scan for: " + scanner);
        }

        String jobId = specright.bulkJob(scanner, token);
        if(debug()) {
            logger.info("Scan job created for '" + scanner + "' with id: " + jobId);
        }

        Event.builder(URI.create(Specright.EVENT_JOB_TRACKING), event)
                .addParameter(SCANNER, scanner)
                .addParameter(JOB_ID, jobId)
                .addParameter(TOKEN, token)
                .create();
    }
}
