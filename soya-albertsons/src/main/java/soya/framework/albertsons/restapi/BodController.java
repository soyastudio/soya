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
@Path("/bod")
@Api(value = "bod")
@GroupMapping(value = "bod")
public class BodController extends CommandDispatcher {

    public BodController() {
        super();
    }

    @POST
    @Path("/mappings-override/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "mappings-override", template = "-o {0} -m {1} -h {workspace.home} -b {2}")
    public Response mappingsOverride(String override, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("mappingsOverride",
                        new Object[]{encodeMessage(override), mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/arrays/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "arrays", template = "-c {0} -m {1} -h {workspace.home} -b {2}")
    public Response arrays(@HeaderParam("construction") String construction, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("arrays",
                        new Object[]{construction, mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/esql-validation/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "esql-validation", template = "-c {0} -m {1} -h {workspace.home} -b {2}")
    public Response esqlValidation(String code, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("esqlValidation",
                        new Object[]{encodeMessage(code), mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/construct-annotate/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "construct-annotate", template = "-m {0} -h {workspace.home} -b {1}")
    public Response constructAnnotate(@HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("constructAnnotate",
                        new Object[]{mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/array-annotate/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "array-annotate", template = "-m {0} -h {workspace.home} -b {1}")
    public Response arrayAnnotate(@HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("arrayAnnotate",
                        new Object[]{mappingFile, businessObject}))
                .build();
    }

    @GET
    @Path("/xlsx-mappings/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "xlsx-mappings", template = "-f {0} -s {1} -h {workspace.home} -b {2}")
    public Response xlsxMappings(@HeaderParam("mappingFile") String mappingFile, @HeaderParam("mappingSheet") String mappingSheet, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("xlsxMappings",
                        new Object[]{mappingFile, mappingSheet, businessObject}))
                .build();
    }

    @GET
    @Path("/schema/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "schema", template = "-h {workspace.home} -b {0}")
    public Response schema(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("schema",
                        new Object[]{businessObject}))
                .build();
    }

    @GET
    @Path("/sample-xml/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "sample-xml", template = "-h {workspace.home} -b {0}")
    public Response sampleXml(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("sampleXml",
                        new Object[]{businessObject}))
                .build();
    }

    @GET
    @Path("/xpath-mappings/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "xpath-mappings", template = "-m {0} -h {workspace.home} -b {1}")
    public Response xpathMappings(@HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("xpathMappings",
                        new Object[]{mappingFile, businessObject}))
                .build();
    }

    @GET
    @Path("/xpath-construct/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "xpath-construct", template = "-c {0} -m {1} -h {workspace.home} -b {2}")
    public Response xpathConstruct(@HeaderParam("construction") String construction, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("xpathConstruct",
                        new Object[]{construction, mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/create/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "create", template = "-h {workspace.home} -b {0}")
    public Response create(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("create",
                        new Object[]{businessObject}))
                .build();
    }

    @GET
    @Path("/json-types/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "json-types", template = "-h {workspace.home} -b {0}")
    public Response jsonTypes(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("jsonTypes",
                        new Object[]{businessObject}))
                .build();
    }

    @GET
    @Path("/json-schema/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "json-schema", template = "-x {0} -h {workspace.home} -b {1}")
    public Response jsonSchema(@HeaderParam("jsonSchemaFile") String jsonSchemaFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("jsonSchema",
                        new Object[]{jsonSchemaFile, businessObject}))
                .build();
    }

    @POST
    @Path("/filter-mapping/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "filter-mapping", template = "-q {0} -m {1} -h {workspace.home} -b {2}")
    public Response filterMapping(@HeaderParam("expression") String expression, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("filterMapping",
                        new Object[]{expression, mappingFile, businessObject}))
                .build();
    }

    @POST
    @Path("/esql-gen/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "esql-gen", template = "-c {0} -m {1} -h {workspace.home} -b {2}")
    public Response esqlGen(@HeaderParam("construction") String construction, @HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("esqlGen",
                        new Object[]{construction, mappingFile, businessObject}))
                .build();
    }

    @GET
    @Path("/mappings-validation/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "mappings-validation", template = "-m {0} -h {workspace.home} -b {1}")
    public Response mappingsValidation(@HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("mappingsValidation",
                        new Object[]{mappingFile, businessObject}))
                .build();
    }

    @GET
    @Path("/avsc/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "avsc", template = "-h {workspace.home} -b {0}")
    public Response avsc(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("avsc",
                        new Object[]{businessObject}))
                .build();
    }

    @POST
    @Path("/json-type-mappings/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "json-type-mappings", template = "-h {workspace.home} -b {0}")
    public Response jsonTypeMappings(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("jsonTypeMappings",
                        new Object[]{businessObject}))
                .build();
    }

    @GET
    @Path("/json-sample/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "json-sample", template = "-x {0} -h {workspace.home} -b {1}")
    public Response jsonSample(@HeaderParam("jsonSchemaFile") String jsonSchemaFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("jsonSample",
                        new Object[]{jsonSchemaFile, businessObject}))
                .build();
    }

    @GET
    @Path("/sample-avro/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "sample-avro", template = "-h {workspace.home} -b {0}")
    public Response sampleAvro(@PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("sampleAvro",
                        new Object[]{businessObject}))
                .build();
    }

    @POST
    @Path("/deprecated-mappings/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "deprecated-mappings", template = "-m {0} -h {workspace.home} -b {1}")
    public Response deprecatedMappings(@HeaderParam("mappingFile") String mappingFile, @PathParam("businessObject") String businessObject) throws Exception {
        return Response
                .ok(_dispatch("deprecatedMappings",
                        new Object[]{mappingFile, businessObject}))
                .build();
    }

}