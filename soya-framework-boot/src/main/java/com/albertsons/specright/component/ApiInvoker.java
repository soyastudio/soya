package com.albertsons.specright.component;

import com.albertsons.specright.service.HttpClientService;
import com.albertsons.specright.service.Specright;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.commons.eventbus.Event;
import soya.framework.commons.eventbus.Subscriber;

import java.net.URI;

@Component("api-invoker")
public class ApiInvoker extends SpecrightComponent {

    @Override
    public void onEvent(Event event) {

        System.out.println("================= ApiInvoker: " + event.getSource().getClass().getName());

        for(int i = 0; i < 5; i ++) {
            try {
                Thread.sleep(300l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Event.builder(URI.create("specright://kafka-producer"), event).create();
        }
    }
}
