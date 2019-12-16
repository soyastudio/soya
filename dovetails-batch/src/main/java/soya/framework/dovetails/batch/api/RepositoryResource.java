package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.dovetails.batch.server.Deployment;
import soya.framework.dovetails.batch.server.PipelineMonitoringService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Path("/deployment")
@Api(value = "Deployment Service")
public class RepositoryResource {
    @Autowired
    PipelineMonitoringService repositoryService;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list() {
        List<Deployment> descriptors = new ArrayList<>();
        for (String d : repositoryService.getDeployments()) {
            descriptors.add(repositoryService.getDeployment(d));
        }
        return Response.status(200).entity(descriptors).build();
    }

    @GET
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deploymentDetails(@PathParam("name") String name) throws IOException {
        return Response.status(200).entity(repositoryService.getDeploymentDetails(name)).build();
    }

    @GET
    @Path("/refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refresh() {
        repositoryService.refresh();
        while (repositoryService.isBusy()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Deployment> descriptors = new ArrayList<>();
        for (String d : repositoryService.getDeployments()) {
            descriptors.add(repositoryService.getDeployment(d));
        }

        return Response.status(200).entity(descriptors).build();
    }

    @POST
    @Path("/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deploy(String json) throws IOException {
        Deployment dd = repositoryService.deploy(json);
        return Response.status(200).entity(dd).build();
    }

    @DELETE
    @Path("/{deployment}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("deployment") String deployment) throws IOException {
        repositoryService.delete(deployment);
        return Response.status(200).build();
    }


}

