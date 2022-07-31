package com.albertsons.specright.component;

import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;

@Component
@Subscriber.ListenTo(Specright.EVENT_RESULT_EXPORT)
public class ScanResultExporter extends SpecrightComponent {
    @Override
    protected void process(Event event) throws Exception {
        byte[] contents = (byte[]) event.getPayload();

        System.out.println("============= " + new String(contents));

    }
}
