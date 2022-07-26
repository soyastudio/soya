package com.albertsons.specright.service;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Specright {
    private static Specright me;

    private Configuration configuration;

    private HttpClientService httpClientService;
    private KafkaService kafkaService;

    private Map<String, Scanner> scannerMap = new ConcurrentHashMap<>();

    protected Specright() {
        if (me != null) {
            throw new IllegalStateException("Instance already exist");
        }

        me = this;
    }

    protected void configure(InputStream inputStream) {
        this.configuration = new Gson().fromJson(new InputStreamReader(inputStream), Configuration.class);
        configuration.scanners.forEach(scanner -> {
            scannerMap.put(scanner.name, scanner);
        });

        httpClientService = new HttpClientService();
        kafkaService = new KafkaService();
    }

    protected long heartbeatDelay() {
        return configuration.scheduler.delay;
    }

    protected long heartbeatPeriod() {
        return configuration.scheduler.period;
    }

    public Set<String> scanners() {
        return scannerMap.keySet();
    }

    public String urlTemplate(String name) {
        return scannerMap.get(name).urlTemplate;
    }

    public String kafkaTopic(String name) {
        return scannerMap.get(name).kafkaTopic;
    }

    public boolean enabled(String name) {
        return scannerMap.get(name).enabled;
    }

    public boolean scanning(String name) {
        return scannerMap.get(name).scanning;
    }

    public void start(String name) {
        scannerMap.get(name).scanning = true;
    }

    public void complete(String name) {
        scannerMap.get(name).scanning = false;
    }

    public boolean readyToWork(String name) {
        return scannerMap.get(name).enabled && scannerMap.get(name).scanning;
    }

    public void reset() {
        scannerMap.values().forEach(e -> {
            e.scanning = true;
        });
    }

    public HttpClientService getHttpClientService() {
        return httpClientService;
    }

    public KafkaService getKafkaService() {
        return kafkaService;
    }

    public static Specright getInstance() {
        return me;
    }

    static class Configuration {

        private Scheduler scheduler;
        private AuthRequest authRequest;
        private List<Scanner> scanners;
    }

    static class Scheduler {
        private long delay;
        private long period;
    }

    static class Kafka {

    }

    static class AuthRequest {

    }

    static class Scanner {
        private String name;
        private String urlTemplate;
        private String kafkaTopic;
        private boolean enabled;
        private boolean scanning;

    }

}
