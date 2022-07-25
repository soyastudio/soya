package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;

@Component("kafka-producer")
public class KafkaProducer extends SpecrightComponent {

    @Override
    public void onEvent(Event event) {

        System.out.println("================= KafkaProducer: " + event.getSource().getClass().getName());
    }

    private void produceMessage() {
        Specright.getInstance().getKafkaService().produce(null, null);
    }
}
