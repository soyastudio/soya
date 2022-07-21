package soya.framework.server;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PipelineServer {

    private static final Logger logger = LoggerFactory.getLogger(PipelineServer.class);
    private static PipelineServer instance;

    private String serverName;
    private File home;
    private File conf;
    private File pipelineHome;

    private EventBus eventBus;

    private Map<String, Pipeline> pipelines = new ConcurrentHashMap<>();

    protected PipelineServer() {
        try {
            init();
            instance = this;

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static PipelineServer getInstance() {
        return instance;
    }

    protected void init() throws URISyntaxException, IOException {
        this.serverName = name();
        this.eventBus = new EventBus(serverName);

        URL url = PipelineServer.class.getProtectionDomain().getCodeSource().getLocation();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            String path = url.getPath();
            int index = path.indexOf(".jar");
            path = path.substring(0, index) + ".jar";
            path = new URI(path).getPath();
            home = new File(path);
            if (home.exists()) {
                home = home.getParentFile().getParentFile();
            }

        } else {
            File userHome = new File(System.getProperty("user.home"));
            home = new File(userHome, "Application/" + serverName);
            if (!home.exists()) {
                home.mkdirs();
            }
        }

        System.setProperty("pipeline.server.home", home.getAbsolutePath());
        conf = new File(home, "conf");
        if (!conf.exists()) {
            conf.mkdirs();
        }
        System.setProperty("pipeline.server.conf.dir", conf.getAbsolutePath());

        File configFile = new File(conf, "server-config.properties");
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        pipelineHome = new File(home, "pipeline");
        if (!pipelineHome.exists()) {
            pipelineHome.mkdirs();
        }
        System.setProperty("pipeline.server.pipeline.dir", pipelineHome.getAbsolutePath());

        logger.info("Pipeline Server Home: {}", home.getAbsolutePath());
        logger.info("Pipeline conf dir: {}", conf.getAbsolutePath());
        logger.info("Pipeline pipeline dir: {}", pipelineHome.getAbsolutePath());

    }

    protected String name() {
        return "pipeline-server";
    }

    public String getServerName() {
        return serverName;
    }

    public File getHome() {
        return home;
    }

    public List<String> pipelines() {
        List<String> list = new ArrayList<>(pipelines.keySet());
        Collections.sort(list);
        return list;
    }

    public Pipeline getPipeline(String name) {
        return pipelines.get(name);
    }

    public void publish(ServiceEvent event) {
        eventBus.post(event);
    }

    public abstract <T> T getService(Class<T> serviceType);

    protected void register(ServiceEventListener... listeners) {
        for (ServiceEventListener listener : listeners) {
            eventBus.register(listener);
        }
    }

    protected void initPipeline() {
        Gson gson = new Gson();
        File[] files = pipelineHome.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File configFile = new File(file, "pipeline.json");
                if (configFile.exists()) {
                    try {
                        InputStream inputStream = new FileInputStream(configFile);
                        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));
                        Pipeline pipeline = gson.fromJson(jsonElement, Pipeline.class);
                        pipelines.put(pipeline.getName(), pipeline);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }

        if (pipelines.isEmpty()) {
            loadFromClasspath();
        }

        pipelines.values().forEach(e -> {
            publish(new PipelineInitializationEvent(e.getName()));
        });
    }

    private void loadFromClasspath() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("pipeline.json");
        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));
        Pipeline[] arr = new Gson().fromJson(jsonElement, Pipeline[].class);
        for (Pipeline pp : arr) {
            pipelines.put(pp.getName(), pp);
        }
    }


}
