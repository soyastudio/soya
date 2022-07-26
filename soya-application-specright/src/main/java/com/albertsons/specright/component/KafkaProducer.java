package com.albertsons.specright.component;

import com.albertsons.specright.eventbus.Event;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightEvent;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KafkaProducer extends SpecrightComponent {
    static final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    @Override
    public void onEvent(Event event) {
        logger.info(Specright.getInstance().kafkaTopic(event.getParameter(SCANNER)) + ": " + event.getPayload());
    }

    private void produceMessage() {
        Specright.getInstance().getKafkaService().produce(null, null);
    }

    @Override
    public SpecrightEvent[] listenTo() {
        return new SpecrightEvent[]{SpecrightEvent.KAFKA_PRODUCE_EVENT};
    }
}
