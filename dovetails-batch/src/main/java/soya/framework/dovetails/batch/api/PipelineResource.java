package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.dovetails.batch.server.PipelineService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
@Path("/pipeline")
@Api(value = "Pipeline Service")
public class PipelineResource {

    @Autowired
    private PipelineService pipelineService;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response jobNames() {
        return Response.status(200).entity(pipelineService.getJobNames()).build();
    }

    @POST
    @Path("/next/{jobName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deploy(@PathParam("jobName") String jobName) throws IOException {
        return Response.status(200).entity(pipelineService.startNext(jobName)).build();
    }
}
