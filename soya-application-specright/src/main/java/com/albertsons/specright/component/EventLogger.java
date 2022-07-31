package com.albertsons.specright.component;

import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Subscriber.ListenTo({Specright.EVENT_JOB_TRACKING, Specright.EVENT_RESULT_EXPORT, Specright.EVENT_Exception_HANDLE})
public class EventLogger extends SpecrightComponent {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());

    @Override
    protected void process(Event event) throws Exception {
        if(Specright.getInstance().debug()) {
            logger.info(event.toURI());
        }
    }
}
