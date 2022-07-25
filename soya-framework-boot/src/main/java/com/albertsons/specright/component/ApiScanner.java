package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;

import java.net.URI;
import java.util.Random;
import java.util.logging.Logger;

@Component("api-scanner")
public class ApiScanner extends SpecrightComponent {
    static final Logger logger = Logger.getLogger(ApiScanner.class.getName());

    @Override
    public void onEvent(Event event) {
        logger.info("Start api scan by fetching auth-token...");
        String token = fetchToken();

        Specright.getInstance().reset();
        Specright.getInstance().scanners().forEach(e -> {
            scan(event, e, token, new Random().nextInt(10));
        });


    }

    protected String fetchToken() {
        return "AUTH-TOKEN";
    }

    protected void scan(Event event, String api, String token, int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (Specright.getInstance().readyToWork(api)) {
            invokeApi(event, api, token);
        }
    }

    protected void invokeApi(Event event, String scanner, String token) {

        for (int i = 0; i < 3; i++) {
            Event evt = Event.builder(URI.create("specright://api-invoker"), event)
                    .addParameter(SCANNER, scanner)
                    .addParameter(TOKEN, token)
                    .addParameter(SKIP, "" + 50 * i)
                    .create();

        }

        Specright.getInstance().complete(scanner);
    }


}
