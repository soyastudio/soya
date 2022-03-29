package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import soya.framework.commons.cli.CommandDispatcher;
import soya.framework.commons.cli.CommandMapping;
import soya.framework.commons.cli.GroupMapping;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Controller
@Path("/ant")
@Api(value = "ant")
@GroupMapping(value = "ant")
public class AntController extends CommandDispatcher {

    public AntController() {
        super();
    }

    @POST
    @Path("/build")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "build", template = "-b {0} -f {1} -t {2} -h {ant.work.home}")
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
    @CommandMapping(command = "mkdir", template = "-d {0} -h {ant.work.home}")
    public Response mkdir(@HeaderParam("dir") String dir) throws Exception {
        return Response
                .ok(_dispatch("mkdir",
                        new Object[]{dir}))
                .build();
    }

}