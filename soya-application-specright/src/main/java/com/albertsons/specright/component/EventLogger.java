package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Subscriber.ListenTo({Specright.EVENT_API_INVOKE, Specright.EVENT_KAFKA_PRODUCE, Specright.EVENT_Exception_HANDLE})
public class EventLogger extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        logger.info(event.toURI());
    }
}
