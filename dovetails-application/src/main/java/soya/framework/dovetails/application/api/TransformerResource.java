package soya.framework.dovetails.application.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/transformer")
@Api(value = "Transform Service")
public class TransformerResource {

    @POST
    @Path("/jolt")
    @Produces(MediaType.TEXT_PLAIN)
    public Response jolt() {
        StringBuilder builder = new StringBuilder();
        builder.append("JOLT").append("\n");
        return Response.status(200).entity(builder.toString()).build();
    }
}
