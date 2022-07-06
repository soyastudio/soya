package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.action.dispatch.ActionForward;
import soya.framework.action.dispatch.ActionDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//@Controller
@Path("/ant")
@Api(value = "ant")
public class AntController extends ActionDispatcher {

    public AntController() {
        super();
    }

    @POST
    @Path("/build")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ActionForward(command = "build -b {0} -f {1} -t {2} -h {ant.work.home}")
    public Response build(@HeaderParam("baseDir") String baseDir, @HeaderParam("buildFile") String buildFile, @HeaderParam("task") String task) throws Exception {
        return Response
                .ok(_dispatch("build",
                        new Object[]{baseDir, buildFile, task}))
                .build();
    }

    @POST
    @Path("/mkdir")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ActionForward(command = "mkdir -d {0} -h {ant.work.home}")
    public Response mkdir(@HeaderParam("dir") String dir) throws Exception {
        return Response
                .ok(_dispatch("mkdir",
                        new Object[]{dir}))
                .build();
    }

}