package soya.framework.server;

import java.io.File;

public class PipelineDeployment {
    private File baseDir;
    private String name;
    private DeploymentState state;

    public PipelineDeployment(File baseDir) {
        this.baseDir = baseDir;
        this.name = baseDir.getName();
        this.state = DeploymentState.CREATED;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public String getName() {
        return name;
    }

    public DeploymentState getState() {
        return state;
    }

    public void setState(DeploymentState state) {
        this.state = state;
    }

    public boolean processing() {
        return DeploymentState.STARTING.equals(state) || DeploymentState.STOPPING.equals(state);
    }

    static enum DeploymentState {
        CREATED, STARTING, STARTED, STOPPING, STOPPED, FAILED
    }
}
