package com.albertsons.specright.component;

import com.albertsons.specright.service.Event;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.Subscriber;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Subscriber.ListenTo(Specright.EVENT_Exception_HANDLE)
public class ExceptionHandler extends SpecrightComponent {

    static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    @Override
    protected void process(Event event) throws Exception {

    }
}
