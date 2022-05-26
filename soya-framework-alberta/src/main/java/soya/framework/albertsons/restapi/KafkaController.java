package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.commandline.CommandMapping;
import soya.framework.commandline.Dispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/kafka")
@Api(value = "kafka")
public class KafkaController extends Dispatcher {

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
    @CommandMapping(command = "produce", template = "-e {0} -t {1} -p {2} -m {3}")
    public Response produce(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout, @HeaderParam("produceTopic") String produceTopic, String message) throws Exception {
        return Response
                .ok(_dispatch("produce",
                        new Object[]{environment, timeout, produceTopic, encodeMessage(message)}))
                .build();
    }

    @GET
    @Path("/topics/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "topics", template = "-e {0} -t {1} -q {2}")
    public Response topics(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout, @HeaderParam("query") String query) throws Exception {
        return Response
                .ok(_dispatch("topics",
                        new Object[]{environment, timeout, query}))
                .build();
    }

    @GET
    @Path("/topic/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "topic", template = "-e {0} -t {1} -c {2}")
    public Response topic(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout, @HeaderParam("topicName") String topicName) throws Exception {
        return Response
                .ok(_dispatch("topic",
                        new Object[]{environment, timeout, topicName}))
                .build();
    }

    @GET
    @Path("/consume/{environment}/{consumeTopic}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "consume", template = "-e {0} -c {1} -t {2} -f {3}")
    public Response consume(@PathParam("environment") String environment, @PathParam("consumeTopic") String consumeTopic, @HeaderParam("timeout") String timeout, @HeaderParam("format") String format) throws Exception {
        return Response
                .ok(_dispatch("consume",
                        new Object[]{environment, consumeTopic, timeout, format}))
                .build();
    }

    @POST
    @Path("/pub-and-sub/{environment}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "pub-and-sub", template = "-e {0} -t {1} -p {2} -c {3} -m {4}")
    public Response pubAndSub(@PathParam("environment") String environment, @HeaderParam("timeout") String timeout, @HeaderParam("produceTopic") String produceTopic, @HeaderParam("consumeTopic") String consumeTopic, String message) throws Exception {
        return Response
                .ok(_dispatch("pubAndSub",
                        new Object[]{environment, timeout, produceTopic, consumeTopic, encodeMessage(message)}))
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