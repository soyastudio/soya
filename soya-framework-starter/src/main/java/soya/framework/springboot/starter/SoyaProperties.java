package soya.framework.springboot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "soya.framework")
public class SoyaProperties {

    private String home;

    private int executorThreadPoolSize = 5;

    private String scanPackages;

    private String apiMappings = "/api/*";

    private boolean debug = false;

    public SoyaProperties() {
        home = new File("").getAbsolutePath().replaceAll("\\\\", "/");
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public int getExecutorThreadPoolSize() {
        return executorThreadPoolSize;
    }

    public void setExecutorThreadPoolSize(int executorThreadPoolSize) {
        this.executorThreadPoolSize = executorThreadPoolSize;
    }

    public String getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    public String getApiMappings() {
        return apiMappings;
    }

    public void setApiMappings(String apiMappings) {
        this.apiMappings = apiMappings;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
