package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.commons.cli.CommandDispatcher;
import soya.framework.commons.cli.CommandMapping;
import soya.framework.commons.cli.GroupMapping;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/resource")
@Api(value = "resource")
@GroupMapping(value = "resource")
public class ResourceController extends CommandDispatcher {

    public ResourceController() {
        super();
    }

    @POST
    @Path("/gzip")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "gzip", template = "-s {0}")
    public Response gzip(String source) throws Exception {
        return Response
                .ok(_dispatch("gzip",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/aes-encrypt")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "aes-encrypt", template = "-k {0} -s {1}")
    public Response aesEncrypt(@HeaderParam("secret") String secret, String source) throws Exception {
        return Response
                .ok(_dispatch("aesEncrypt",
                        new Object[]{secret, encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/aes-decrypt")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "aes-decrypt", template = "-k {0} -s {1}")
    public Response aesDecrypt(@HeaderParam("secret") String secret, String source) throws Exception {
        return Response
                .ok(_dispatch("aesDecrypt",
                        new Object[]{secret, encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/mustache-attribute")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "mustache-attribute", template = "-s {0}")
    public Response mustacheAttribute(String source) throws Exception {
        return Response
                .ok(_dispatch("mustacheAttribute",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/base64-encode")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "base64-encode", template = "-s {0}")
    public Response base64Encode(String source) throws Exception {
        return Response
                .ok(_dispatch("base64Encode",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/echo")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "echo", template = "-s {0}")
    public Response echo(String source) throws Exception {
        return Response
                .ok(_dispatch("echo",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/unzip")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "unzip", template = "-s {0}")
    public Response unzip(String source) throws Exception {
        return Response
                .ok(_dispatch("unzip",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/base64-decode")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "base64-decode", template = "-s {0}")
    public Response base64Decode(String source) throws Exception {
        return Response
                .ok(_dispatch("base64Decode",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/json-format")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "json-format", template = "-s {0}")
    public Response jsonFormat(String source) throws Exception {
        return Response
                .ok(_dispatch("jsonFormat",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

    @POST
    @Path("/extract")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "extract", template = "-s {0}")
    public Response extract(String source) throws Exception {
        return Response
                .ok(_dispatch("extract",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

}