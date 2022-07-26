package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.service.SpecrightEvent;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class FailedEventHandler extends SpecrightComponent {

    static final Logger logger = Logger.getLogger(FailedEventHandler.class.getName());

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public SpecrightEvent[] listenTo() {
        return new SpecrightEvent[]{SpecrightEvent.Exception_HANDLE_EVENT};
    }
}
