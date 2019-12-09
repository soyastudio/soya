package soya.framework.dovetails.batch.service;

public class DeploymentDescriptor {
    private String name;

    public DeploymentDescriptor(Deployment deployment) {
        this.name = deployment.getName();
    }

    public String getName() {
        return name;
    }
}
