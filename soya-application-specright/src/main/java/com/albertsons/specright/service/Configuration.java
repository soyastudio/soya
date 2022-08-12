package com.albertsons.specright.service;

import java.util.Properties;

public abstract class Configuration {
    public static final String HEARTBEAT_DELAY = "heartbeatDelay";
    public static final String HEARTBEAT_PERIOD = "heartbeatPeriod";
    public static final String REFRESH_FREQUENCY = "refreshFrequency";
    public static final String DEDUG = "debug";

    public static final String SPECRIGHT_PASSWORD = "password";
    public static final String SPECRIGHT_USERNAME = "username";
    public static final String SPECRIGHT_USER_ID = "userId";
    public static final String SPECRIGHT_API_KEY = "apiKey";
    public static final String SPECRIGHT_ACCESS_TOKEN = "accessToken";
    public static final String SPECRIGHT_REFRESH_TOKEN = "refreshToken";
    public static final String SPECRIGHT_HOST = "host";
    public static final String SPECRIGHT_AUTH_HOST = "authHost";

    public static final String AZURE_BLOB_STORAGE_ACCOUNT_NAME = "azBlobAccountName";
    public static final String AZURE_BLOB_STORAGE_ACCOUNT_KEY = "azBlobAccountKey";
    public static final String AZURE_BLOB_STORAGE_ENDPOINT = "azBlobEndpoint";
    public static final String AZURE_BLOB_STORAGE_CONTAINER_NAME = "azBlobContainerName";
    public static final String AZURE_BLOB_STORAGE_BASE_DIR = "azBlobBaseDir";

    private static Configuration INSTANCE;

    private Properties properties = new Properties();

    protected Configuration(Properties properties) {
        if(INSTANCE != null) {
            throw new IllegalStateException("Configuration instance already exist!");
        }

        this.properties.putAll(properties);
        INSTANCE = this;
    }

    private static Configuration getInstance() {
        if(INSTANCE == null) {
            throw new  IllegalStateException("Configuration is not created!");
        }

        return INSTANCE;
    }

    public static String get(String propName) {
        return getInstance().properties.getProperty(propName);
    }
}
