package soya.framework.server;

import java.io.File;

public class PipelineDeploymentEvent extends ServiceEvent {

    public static final String[] DEPLOYMENTS = new String[] {".zip"};
    public static final String START_EXTENSION = "start";
    public static final String STOP_EXTENSION = "stop";
    public static final String DELETE_EXTENSION = "delete";

    private final File file;

    public PipelineDeploymentEvent(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
