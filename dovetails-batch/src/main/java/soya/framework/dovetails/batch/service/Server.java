package soya.framework.dovetails.batch.service;

import java.io.File;
import java.util.Properties;

public abstract class Server {
    protected static Server instance;

    protected File home;
    protected Properties configuration = new Properties();

    protected Server() {
    }

    public File getHome() {
        return home;
    }

    public String getConfiguration(String key) {
        return configuration.getProperty(key);
    }

    public abstract void publish(ServiceEvent event);

    public static Server getInstance() {
        return instance;
    }
}
