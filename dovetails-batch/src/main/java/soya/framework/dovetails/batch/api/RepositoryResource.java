package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.dovetails.batch.service.DeploymentDescriptor;
import soya.framework.dovetails.batch.service.DeploymentService;

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
    DeploymentService repositoryService;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list() {
        List<DeploymentDescriptor> descriptors = new ArrayList<>();
        for (String d : repositoryService.getDeployments()) {
            descriptors.add(new DeploymentDescriptor(repositoryService.getDeployment(d)));
        }
        return Response.status(200).entity(descriptors).build();
    }

    @GET
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deploymentDetails(@PathParam("name") String name) throws IOException {
        return Response.status(200).entity(repositoryService.getDeploymentDetails(name)).build();
    }

    @POST
    @Path("/deploy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deploy(String json) throws IOException {
        DeploymentDescriptor dd = repositoryService.deploy(json);
        return Response.status(200).entity(dd).build();
    }
}
