package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.core.CommandMapping;
import soya.framework.core.Dispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/edis")
@Api(value = "Business Object Development Service")
public class BusinessObjectResource extends Dispatcher {

    public BusinessObjectResource() {
        super();
    }

    @POST
    @Path("/create/{bod}")
    @Produces({MediaType.APPLICATION_JSON})
    @CommandMapping(command = "create", template = "-r {workspace.home} -b {0}")
    public Response create(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("create", new Object[]{bod})).build();
    }

    // =========================== requirement

    @GET
    @Path("/schema/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "schema", template = "-r {workspace.home} -b {0}")
    public Response schema(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("schema", new Object[]{bod})).build();
    }

    @GET
    @Path("/sample-xml/{bod}")
    @Produces({MediaType.APPLICATION_XML})
    @CommandMapping(command = "sample-xml", template = "-r {workspace.home} -b {0}")
    public Response sampleXml(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("sampleXml", new Object[]{bod})).build();
    }

    @GET
    @Path("/json-types/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "json-types", template = "-r {workspace.home} -b {0}")
    public Response jsonTypes(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("jsonTypes", new Object[]{bod})).build();
    }

    @GET
    @Path("/avsc/{bod}")
    @Produces({MediaType.APPLICATION_JSON})
    @CommandMapping(command = "avsc", template = "-r {workspace.home} -b {0}")
    public Response avsc(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("avsc", new Object[]{bod})).build();
    }

    @GET
    @Path("/sample-avro/{bod}")
    @Produces({MediaType.APPLICATION_XML})
    @CommandMapping(command = "sample-avro", template = "-r {workspace.home} -b {0}")
    public Response sampleAvro(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("sampleAvro", new Object[]{bod})).build();
    }

    @GET
    @Path("/source-schema/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "json-schema", template = "-r {workspace.home} -b {0}")
    public Response sourceSchema(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("sourceSchema", new Object[]{bod})).build();
    }

    @POST
    @Path("/sample-source-json/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "json-sample", template = "-r {workspace.home} -b {0}")
    public Response jsonSample(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("jsonSample", new Object[]{bod})).build();
    }

    @GET
    @Path("/xlsx-mappings/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "xlsx-mapping", template = "-r {workspace.home} -b {0}")
    public Response xlsx(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("xlsx", new Object[]{bod})).build();
    }

    // =========================== mapping

    @GET
    @Path("/mappings/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "xpath-mappings", template = "-r {workspace.home} -b {0}")
    public Response mappings(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("mappings", new Object[]{bod})).build();
    }

    @GET
    @Path("/mappings-validate/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "mappings-validate", template = "-r {workspace.home} -b {0}")
    public Response validateMappings(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("validateMappings", new Object[]{bod})).build();
    }

    @POST
    @Path("/mappings-override/{bod}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "mappings-override", template = "-r {workspace.home} -b {0} -o {1}")
    public Response overrideMappings(@PathParam("bod") String bod, String override) throws Exception {
        return Response.ok(_dispatch("overrideMappings", new Object[]{bod, encodeMessage(override)})).build();
    }

    // =========================== construct

    @GET
    @Path("/construct/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "construct", template = "-r {workspace.home} -b {0} -c xpath-construct.properties")
    public Response construct(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("construct", new Object[]{bod})).build();
    }

    @GET
    @Path("/construct-annotate/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "construct-annotate", template = "-r {workspace.home} -b {0}")
    public Response constructAnnotate(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("constructAnnotate", new Object[]{bod})).build();
    }

    @GET
    @Path("/array-annotate/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "array-annotate", template = "-r {workspace.home} -b {0}")
    public Response arrayAnnotate(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("arrayAnnotate", new Object[]{bod})).build();
    }

    @GET
    @Path("/arrays/{bod}")
    @Produces({MediaType.APPLICATION_JSON})
    @CommandMapping(command = "arrays", template = "-r {workspace.home} -b {0} -c xpath-construct.properties")
    public Response arrays(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("arrays", new Object[]{bod})).build();
    }

    @GET
    @Path("/esql-gen/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "esql-gen", template = "-r {workspace.home} -b {0}")
    public Response esqlGen(@PathParam("bod") String bod) throws Exception {
        return Response.ok(_dispatch("esqlGen", new Object[]{bod})).build();
    }

    @POST
    @Path("/esql-validate/{bod}")
    @Produces({MediaType.TEXT_PLAIN})
    @CommandMapping(command = "esql-validate", template = "-r {workspace.home} -b {0} -c {1}")
    public Response esqlValidate(@PathParam("bod") String bod, String contents) throws Exception {
        String encoded = new String(Base64.getEncoder().encode(contents.getBytes(StandardCharsets.UTF_8)));
        return Response.ok(_dispatch("esqlValidate", new Object[]{bod, encoded})).build();
    }


}
