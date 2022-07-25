package com.albertsons.specright.component;

import com.albertsons.specright.service.HttpClientService;
import com.albertsons.specright.service.KafkaService;
import com.albertsons.specright.service.Specright;
import soya.framework.commons.eventbus.Event;
import soya.framework.commons.eventbus.Subscriber;

public abstract class SpecrightComponent implements Subscriber {


    protected HttpClientService httpClientService() {
        return Specright.getInstance().getHttpClientService();
    }

    protected KafkaService kafkaService() {
        return Specright.getInstance().getKafkaService();
    }

    protected String scanner(Event event) {
        return null;
    }
}
