package com.albertsons.specright.component;

import com.albertsons.specright.service.Specright;
import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;

import java.util.logging.Logger;

@Component("kafka-producer")
public class KafkaProducer extends SpecrightComponent {
    static final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    @Override
    public void onEvent(Event event) {
        logger.info(Specright.getInstance().kafkaTopic(event.getParameter(SCANNER)) + ": " + event.getPayload());
    }

    private void produceMessage() {
        Specright.getInstance().getKafkaService().produce(null, null);
    }
}
