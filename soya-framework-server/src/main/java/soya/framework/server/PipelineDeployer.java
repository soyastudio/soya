package soya.framework.server;

import java.io.File;
import java.io.IOException;

public interface PipelineDeployer {
    boolean deployable(File dir);

    PipelineDeployment create(File dir);

    PipelineDeployment deploy(File src, File dest) throws IOException;

    void start(PipelineDeployment deployment);

    void stop(PipelineDeployment deployment);

    void delete(PipelineDeployment deployment);
}
