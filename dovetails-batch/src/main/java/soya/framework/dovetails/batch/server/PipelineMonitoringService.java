package soya.framework.dovetails.batch.server;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PipelineMonitoringService {

    public static final String CONFIG_FILE_NAME = "pipeline.json";

    private File home;
    private Map<String, Deployment> deployments = new ConcurrentHashMap<>();

    protected PipelineMonitoringService(File home) {
        this.home = home;
    }

    public boolean scanning;

    public boolean isBusy() {
        return scanning;
    }

    public synchronized void refresh() {
        if(scanning) {
            while(scanning) {
                System.out.println("---------------- busy!");
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                refresh();
            }

        } else {
            this.scanning = true;
            ((Runnable) () -> {
                long start = System.currentTimeMillis();
                File[] files = home.listFiles();
                for (File dir : files) {

                    File dm = new File(dir, ".dm");
                    if(dm.exists()) {
                        try {
                            FileUtils.forceDelete(dm);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    if (!deployments.containsKey(dir.getName()) && dir.isDirectory()) {
                        File wf = new File(dir, CONFIG_FILE_NAME);
                        if (wf.exists() && wf.isFile()) {
                            String name = dir.getName();
                            if (!deployments.containsKey(name)) {
                                Deployment deployment = new Deployment(dir, wf);
                                deployments.put(name, deployment);

                            }
                        } else  {
                            try {
                                wf.createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                long timestamp = System.currentTimeMillis();
                List<String> ls = new ArrayList<>(deployments.keySet());
                ls.forEach(e -> {
                    Deployment deployment = deployments.get(e);
                    Deployment.State state = deployment.refresh(timestamp);
                    if (state.equals(Deployment.State.NEW)) {
                        Server.getInstance().publish(new DeploymentEvent(deployment));

                    } else if (state.equals(Deployment.State.UPDATED)) {
                        Server.getInstance().publish(new DeploymentEvent(deployment));

                    } else if (state.equals(Deployment.State.REMOVED)) {
                        Server.getInstance().publish(new DeploymentEvent(deployment));
                        deployments.remove(deployment.getName());
                    }
                });

                this.scanning = false;

            }).run();

        }

    }

    public void delete(String name) {
        File dir = new File(home, name);
        if(dir.exists()) {
            try {
                FileUtils.forceDelete(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String[] getDeployments() {
        List<String> list = new ArrayList<>(deployments.keySet());
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    public Deployment deploy(String json) throws IOException {
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject obj = jsonElement.getAsJsonObject();

        JsonObject metadata = obj.get("metadata").getAsJsonObject();
        String name = metadata.get("name").getAsString();
        if (!deployments.containsKey(name)) {
            File dir = new File(home, name);
            dir.mkdirs();
            File configFile = new File(CONFIG_FILE_NAME);

            configFile.createNewFile();

            CharSink sink = Files.asCharSink(configFile, Charsets.UTF_8);
            sink.write(json);

            Deployment deployment = new Deployment(dir, configFile);
            return deployment;
        }

        return null;
    }

    public String getDeploymentDetails(String name) throws IOException {
        return deployments.containsKey(name) ? deployments.get(name).getContents() : null;
    }

    public Deployment getDeployment(String name) {
        return deployments.get(name);
    }


}