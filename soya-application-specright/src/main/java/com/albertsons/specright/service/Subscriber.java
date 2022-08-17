package com.albertsons.specright.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Subscriber {
    void onEvent(Event event);

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ListenTo {
        String[] value();
    }
}
