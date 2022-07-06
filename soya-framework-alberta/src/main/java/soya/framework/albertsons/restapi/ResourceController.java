package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.action.dispatch.ActionForward;
import soya.framework.action.dispatch.ActionDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/resource")
@Api(value = "resource")
public class ResourceController extends ActionDispatcher {

    public ResourceController() {
        super();
    }

    @POST
    @Path("/gzip")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ActionForward(command = "gzip -s {0}")
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
    @ActionForward(command = "aes-encrypt -k {0} -s {1}")
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
    @ActionForward(command = "aes-decrypt -k {0} -s {1}")
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
    @ActionForward(command = "mustache-attribute -s {0}")
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
    @ActionForward(command = "base64-encode -s {0}")
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
    @ActionForward(command = "echo -s {0}")
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
    @ActionForward(command = "unzip -s {0}")
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
    @ActionForward(command = "base64-decode -s {0}")
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
    @ActionForward(command = "json-format -s {0}")
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
    @ActionForward(command = "extract -s {0}")
    public Response extract(String source) throws Exception {
        return Response
                .ok(_dispatch("extract",
                        new Object[]{encodeMessage(source)}))
                .build();
    }

}