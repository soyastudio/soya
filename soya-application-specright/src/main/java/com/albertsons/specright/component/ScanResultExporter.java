package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.eventbus.Event;
import com.albertsons.specright.service.eventbus.Subscriber;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Subscriber.ListenTo(Specright.EVENT_RESULT_EXPORT)
public class ScanResultExporter extends SpecrightComponent {
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    @Override
    protected void process(Event event) throws Exception {
        byte[] contents = (byte[]) event.getPayload();

        System.out.println(event.getParameter(SCANNER) + "_" + DATE_FORMAT.format(new Date()) + ".csv");
        System.out.println(contents.length);

    }
}
