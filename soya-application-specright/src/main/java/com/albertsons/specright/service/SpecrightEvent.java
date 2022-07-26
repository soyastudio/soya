package com.albertsons.specright.service;

public enum SpecrightEvent {
    HEARTBEAT_EVENT("specright://heartbeat"),
    API_SCAN_EVENT("specright://api-scanner"),
    API_INVOKE_EVENT("specright://api-invoker"),
    KAFKA_PRODUCE_EVENT("specright://kafka-producer"),
    Exception_HANDLE_EVENT("specright://exception-handler");

    private final String uri;

    SpecrightEvent(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
