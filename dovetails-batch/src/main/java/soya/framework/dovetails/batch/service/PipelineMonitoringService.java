package soya.framework.dovetails.batch.service;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                File[] files = home.listFiles();
                for (File dir : files) {
                    if (!deployments.containsKey(dir.getName()) && dir.isDirectory()) {
                        File wf = new File(dir, CONFIG_FILE_NAME);
                        if (wf.exists() && wf.isFile()) {
                            String name = dir.getName();
                            if (!deployments.containsKey(name)) {
                                Deployment deployment = new Deployment(dir, wf);
                                deployments.put(name, deployment);

                            }
                        }
                    }
                }

                long timestamp = System.currentTimeMillis();
                List<String> ls = new ArrayList<>(deployments.keySet());
                ls.forEach(e -> {
                    Deployment deployment = deployments.get(e);
                    Deployment.DeploymentState state = deployment.refresh(timestamp);
                    System.out.println("---------- " + deployment.getName() + ": " + state);

                    if (state.equals(Deployment.DeploymentState.NEW)) {
                        deploy(deployment);

                    } else if (state.equals(Deployment.DeploymentState.UPDATED)) {
                        redeploy(deployment);

                    } else if (state.equals(Deployment.DeploymentState.REMOVED)) {
                        undeploy(deployment);
                    }
                });
                this.scanning = false;

            }).run();

        }

    }

    private void deploy(Deployment deployment) {
        deployment.setState(Deployment.DeploymentState.DEPLOYED);
    }

    private void redeploy(Deployment deployment) {
        System.out.println("============== redeploying...");
        deployment.setState(Deployment.DeploymentState.DEPLOYED);
    }

    private void undeploy(Deployment deployment) {

    }

    public String[] getDeployments() {
        List<String> list = new ArrayList<>(deployments.keySet());
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    public DeploymentDescriptor deploy(String json) throws IOException {
        JsonElement jsonElement = new JsonParser().parse(json);
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
            return new DeploymentDescriptor(deployment);
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
