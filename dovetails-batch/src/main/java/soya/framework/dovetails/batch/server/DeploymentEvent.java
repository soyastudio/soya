package soya.framework.dovetails.batch.server;

public class DeploymentEvent extends TraceableEvent {
    private final Deployment deployment;

    public DeploymentEvent(Deployment deployment) {
        super();
        this.deployment = deployment;
    }

    public Deployment getDeployment() {
        return deployment;
    }
}
