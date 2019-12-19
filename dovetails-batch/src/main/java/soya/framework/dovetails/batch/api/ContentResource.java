package soya.framework.dovetails.batch.api;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import soya.framework.dovetails.batch.server.GithubService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
@Path("/cms")
@Api(value = "Content Service")
public class ContentResource {

    @Autowired
    private GithubService service;

    @POST
    @Path("/check-out")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkOut(@HeaderParam("user") String user, @HeaderParam("uri") String uri) throws IOException {
        service.checkOut(user, uri);
        return Response.status(200).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response addNew(@HeaderParam("user") String user, @HeaderParam("uri") String uri, @HeaderParam("checkout") boolean checkout) throws IOException {
        service.addNew(user, uri, checkout);
        return Response.status(200).build();
    }

    @PUT
    @Path("/save")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response saveContent(@HeaderParam("user") String user, @HeaderParam("uri") String uri, String contents) throws IOException {
        service.saveContents(user, uri, contents);
        return Response.status(200).build();
    }

    @POST
    @Path("/check-in")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response checkIn(@HeaderParam("user") String user, @HeaderParam("uri") String uri) throws IOException {
        service.checkIn(user, uri);
        return Response.status(200).build();
    }

    @DELETE
    @Path("/workspace")
    public Response cleanWorkspace(@HeaderParam("user") String user) {
        service.cleanWorkspace(user);
        return Response.status(200).build();
    }

    @DELETE
    @Path("/workspace/clear")
    public Response clearWorkspace() {
        service.clearWorkspace();
        return Response.status(200).build();
    }

    @POST
    @Path("/workspace/import")
    public Response importToWorkspace(@HeaderParam("user") String user, @HeaderParam("path") String path, @HeaderParam("uri") String uri) {
        service.importToWorkspace(user, path, uri);
        return Response.status(200).build();
    }
}
