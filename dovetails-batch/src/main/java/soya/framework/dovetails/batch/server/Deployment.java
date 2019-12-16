package soya.framework.dovetails.batch.server;

import com.google.gson.GsonBuilder;

import java.io.File;

public class Deployment {

    private File base;
    private File configFile;
    private Pipeline pipeline;

    private State state;
    private long lastCheckedTime;

    public Deployment(File base, File configFile) {
        this.base = base;
        this.configFile = configFile;
        this.pipeline = Pipeline.fromJson(configFile);
        this.state = State.NEW;
        this.lastCheckedTime = System.currentTimeMillis();

        if(pipeline == null) {
            pipeline = new Pipeline();
        }
        pipeline.setName(base.getName());
    }

    public String getName() {
        return base.getName();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean deployable() {
        return state == State.NEW || state == State.UPDATED || state == State.REMOVED;
    }

    public long getLastCheckedTime() {
        return lastCheckedTime;
    }

    public File getBase() {
        return base;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public String getContents() {
        if (pipeline == null) {
            return null;
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(pipeline);
    }

    public State refresh(long timestamp) {
        if(!State.DEPLOYED.equals(state)) {
            return state;

        } else if (!configFile.exists()) {
            this.state = State.REMOVED;

        } else if (configFile.lastModified() > lastCheckedTime) {
            this.state = State.UPDATED;
        }

        this.lastCheckedTime = timestamp;
        return state;
    }

    public static enum State {
        NEW, DEPLOYING, DEPLOYED, UPDATED, REMOVING, REMOVED;
    }

}