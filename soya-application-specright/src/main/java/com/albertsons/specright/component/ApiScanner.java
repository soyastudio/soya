package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Random;

@Component
@Subscriber.ListenTo(Specright.EVENT_HEARTBEAT)
public class ApiScanner extends SpecrightComponent {

    @Override
    protected void process(Event event) throws Exception {
        String token = fetchToken();

        Specright.getInstance().reset();
        Specright.getInstance().scanners().forEach(scanner -> {
            try {
                Thread.sleep(new Random().nextInt(10000));

            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            scan(scanner, token, event);

        });
    }

    protected String fetchToken() throws Exception {
        return "AUTH-TOKEN";
    }

    protected void scan(String scanner, String token, Event event) {
        while (Specright.getInstance().readyToWork(scanner)) {
            invokeApi(event, scanner, token);
        }
    }

    protected void invokeApi(Event event, String scanner, String token) {
        for (int i = 0; i < 3; i++) {
            Event evt = Event.builder(URI.create(Specright.EVENT_API_INVOKE), event)
                    .addParameter(SCANNER, scanner)
                    .addParameter(TOKEN, token)
                    .addParameter(SKIP, "" + 50 * i)
                    .create();
        }

        Specright.getInstance().complete(scanner);
    }
}
