package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.dovetails.batch.service.TransformService;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/transformer")
@Api(value = "Transform Service")
public class TransformerResource {
    @Autowired
    private TransformService transformService;

    @POST
    @Path("/jolt")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jolt(@HeaderParam("uri") String uri, String src) {
        String result = transformService.jolt(uri, src);
        return Response.status(200).entity(result).build();
    }
}
