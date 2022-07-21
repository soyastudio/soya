package soya.framework.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DefaultDeployer implements PipelineDeployer {

    @Override
    public boolean deployable(File dir) {
        return true;
    }

    @Override
    public PipelineDeployment create(File dir) {
        return new PipelineDeployment(dir);
    }

    @Override
    public PipelineDeployment deploy(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            File dir = copyDirectory(src, dest);
            PipelineDeployment deployment = create(dir);
            start(deployment);
            return deployment;
        }

        return null;
    }

    @Override
    public void start(PipelineDeployment deployment) {
        if (deployment.processing()) {
            waitForDeploymentReady(deployment, 3000L);
        }

        if (PipelineDeployment.DeploymentState.CREATED.equals(deployment.getState())
                || PipelineDeployment.DeploymentState.STOPPED.equals(deployment.getState())) {

            ((Runnable) () -> {
                try {
                    doStart(deployment);
                    deployment.setState(PipelineDeployment.DeploymentState.STARTED);

                } catch (Exception e) {
                    deployment.setState(PipelineDeployment.DeploymentState.FAILED);
                }
            }).run();

        } else {
            // TODO:
        }

    }

    @Override
    public void stop(PipelineDeployment deployment) {
        if (deployment.processing()) {
            waitForDeploymentReady(deployment, 30000L);
        }

        if (PipelineDeployment.DeploymentState.STARTED.equals(deployment.getState())) {
            deployment.setState(PipelineDeployment.DeploymentState.STOPPING);
            ((Runnable) () -> {
                try {
                    doStop(deployment);

                    deployment.setState(PipelineDeployment.DeploymentState.STOPPED);

                } catch (Exception e) {
                    deployment.setState(PipelineDeployment.DeploymentState.FAILED);
                }
            }).run();
        }

    }

    @Override
    public void delete(PipelineDeployment deployment) {
        if (deployment.processing()) {
            waitForDeploymentReady(deployment, 30000L);
        }


        try {
            FileUtils.forceDelete(deployment.getBaseDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void doStart(PipelineDeployment deployment) throws Exception {

    }

    protected void doStop(PipelineDeployment deployment) throws Exception {

    }

    protected void waitForDeploymentReady(PipelineDeployment deployment, long timeout) throws DeploymentTimeoutException {
        long timestamp = System.currentTimeMillis();
        while (deployment.processing()) {
            if (System.currentTimeMillis() - timestamp > timeout) {
                throw new DeploymentTimeoutException();
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new DeploymentException(e);
            }
        }

    }

    protected File copyDirectory(File src, File dest) throws IOException {
        File dir = new File(dest, src.getName());
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileUtils.copyDirectory(src, dir);

        return dir;
    }
}
