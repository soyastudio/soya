package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

@Component
@Subscriber.ListenTo(Specright.EVENT_HEARTBEAT)
public class ScanTrigger extends SpecrightComponent {

    @Override
    protected void process(Event event) throws Exception {
        Specright.Token token = Specright.getInstance().fetchToken();

        Specright.getInstance().scanners().forEach(scanner -> {
            try {
                Thread.sleep(new Random().nextInt(10000));

            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            scan(scanner, token, event);

        });
    }

    protected void scan(String scanner, Specright.Token token, Event event)  {
        try {
            String jobId = Specright.getInstance().bulkJob(scanner, token);
            Event.builder(URI.create(Specright.EVENT_JOB_TRACKING), event)
                    .addParameter(SCANNER, scanner)
                    .addParameter(JOB_ID, jobId)
                    .addParameter(TOKEN, token.getAccessToken())
                    .create();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
