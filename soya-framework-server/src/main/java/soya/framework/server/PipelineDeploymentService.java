package soya.framework.server;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineDeploymentService implements ServiceEventListener<PipelineDeploymentEvent> {
    private File pipelineHome;
    private File deploymentDir;
    private Map<String, PipelineDeployment> deployments = new ConcurrentHashMap<>();
    private PipelineDeployer deployer = new DefaultDeployer();

    // @PostConstruct
    void init() {
        pipelineHome = new File(System.getProperty("pipeline.server.pipeline.dir"));
        if (!pipelineHome.exists()) {
            pipelineHome.mkdirs();
        }

        deploymentDir = new File(PipelineServer.getInstance().getHome(), "deploy");
        if (!deploymentDir.exists()) {
            deploymentDir.mkdirs();
        }

        File[] files = pipelineHome.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                PipelineDeployment deployment = deployer.create(file);
                if (deployment != null) {
                    deployments.put(deployment.getName(), deployment);
                }
            }
        }


        Timer timer = new Timer();
        deployments.values().forEach(e -> {
            timer.schedule(new DeploymentInitializer(e, deployer), new Random().nextInt(50000));

        });
        timer.schedule(new DeploymentScanner(deploymentDir), 60000L, 15000L);
    }

    @Subscribe
    public void onEvent(PipelineDeploymentEvent event) {
        File file = event.getFile();
        if (file.isDirectory() && deployer.deployable(file)) {
            try {
                PipelineDeployment deployment = deployer.deploy(file, pipelineHome);
                if (deployment != null) {
                    deployments.put(deployment.getName(), deployment);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String extension = Files.getFileExtension(file.getName());
            String pipeline = file.getName().substring(0, file.getName().lastIndexOf("." + extension));
            PipelineDeployment deployment = deployments.get(pipeline);
            if (deployment == null) {

            } else {
                switch (extension) {
                    case PipelineDeploymentEvent.START_EXTENSION:
                        deployer.start(deployment);
                        break;
                    case PipelineDeploymentEvent.STOP_EXTENSION:
                        deployer.stop(deployment);
                        break;
                    case PipelineDeploymentEvent.DELETE_EXTENSION:
                        deployer.delete(deployment);
                        break;

                    default:

                }
            }
        }

        try {
            if (file.getParentFile().equals(deploymentDir)) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class DeploymentInitializer extends TimerTask {
        private PipelineDeployment deployment;
        private PipelineDeployer deployer;

        private DeploymentInitializer(PipelineDeployment deployment, PipelineDeployer deployer) {
            this.deployment = deployment;
            this.deployer = deployer;
        }

        @Override
        public void run() {
            deployer.start(deployment);
        }
    }

    static class DeploymentScanner extends TimerTask {
        private File base;

        DeploymentScanner(File base) {
            this.base = base;
        }

        @Override
        public void run() {
            File[] files = base.listFiles();
            for (File file : files) {
                PipelineServer.getInstance().publish(new PipelineDeploymentEvent(file));
            }
        }
    }

}
