package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.dovetails.batch.server.EventStoreManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/events")
@Api(value = "Event Store Service")
public class EventStoreResource {
    @Autowired
    private EventStoreManager eventStoreManager;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.status(200).entity(eventStoreManager.list()).build();
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") String id) {
        return Response.status(200).entity(eventStoreManager.get(id)).build();
    }
}
