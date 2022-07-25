package com.albertsons.specright.component;

import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;

import java.net.URI;
import java.util.logging.Logger;

@Component("exception-handler")
public class FailedEventHandler extends SpecrightComponent {

    static final Logger logger = Logger.getLogger(FailedEventHandler.class.getName());

    @Override
    public void onEvent(Event event) {
    }
}
