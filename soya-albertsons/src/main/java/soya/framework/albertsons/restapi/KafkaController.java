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
@Path("/kafka")
@Api(value = "kafka")
@GroupMapping(value = "kafka")
public class KafkaController extends CommandDispatcher {

    public KafkaController() {
        super();
    }

    @GET
    @Path("/poll-to-end/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "poll-to-end", template = "-e {0} -t {1}")
    public Response pollToEnd(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("pollToEnd",
                        new Object[]{environment, timeout}))
                .build();
    }

    @POST
    @Path("/produce/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "produce", template = "-p {0} -m {1} -e {2} -t {3}")
    public Response produce(@HeaderParam("produceTopic") String produceTopic, String message, @PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("produce",
                        new Object[]{produceTopic, encodeMessage(message), environment, timeout}))
                .build();
    }

    @GET
    @Path("/topics/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "topics", template = "-q {0} -e {1} -t {2}")
    public Response topics(@HeaderParam("query") String query, @PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("topics",
                        new Object[]{query, environment, timeout}))
                .build();
    }

    @GET
    @Path("/topic/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "topic", template = "-c {0} -e {1} -t {2}")
    public Response topic(@HeaderParam("topicName") String topicName, @PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("topic",
                        new Object[]{topicName, environment, timeout}))
                .build();
    }

    @GET
    @Path("/consume/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "consume", template = "-c {0} -f {1} -e {2} -t {3}")
    public Response consume(@HeaderParam("consumeTopic") String consumeTopic, @HeaderParam("format") String format, @PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("consume",
                        new Object[]{consumeTopic, format, environment, timeout}))
                .build();
    }

    @POST
    @Path("/pub-and-sub/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "pub-and-sub", template = "-c {0} -p {1} -m {2} -e {3} -t {4}")
    public Response pubAndSub(@HeaderParam("consumeTopic") String consumeTopic, @HeaderParam("produceTopic") String produceTopic, String message, @PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("pubAndSub",
                        new Object[]{consumeTopic, produceTopic, encodeMessage(message), environment, timeout}))
                .build();
    }

    @GET
    @Path("/metrics/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "metrics", template = "-e {0} -t {1}")
    public Response metrics(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout) throws Exception {
        return Response
                .ok(_dispatch("metrics",
                        new Object[]{environment, timeout}))
                .build();
    }

}