package soya.framework.dovetails.batch.service;

import com.google.gson.GsonBuilder;

import java.io.File;

public class Deployment {
    private File base;
    private File configFile;
    private Pipeline pipeline;

    public Deployment(File base, File configFile) {
        this.base = base;
        this.configFile = configFile;
        this.pipeline = Pipeline.fromJson(configFile);

    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public String getName() {
        return base.getName();
    }

    public String getContents() {
        if (pipeline == null) {
            return null;
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(pipeline);
    }

}
