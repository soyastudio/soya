package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.commons.cli.CommandDispatcher;
import soya.framework.commons.cli.CommandMapping;
import soya.framework.commons.cli.GroupMapping;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    @CommandMapping(command = "mappings-override", template = "-h {workspace.home} -b {0} -m {1} -o {2}")
    public Response mappingsOverride(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, String override) throws Exception {
        return Response
                .ok(_dispatch("mappingsOverride",
                        new Object[]{businessObject, mappingFile, encodeMessage(override)}))
                .build();
    }

    @POST
    @Path("/arrays/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "arrays", template = "-h {workspace.home} -b {0} -m {1} -c {2}")
    public Response arrays(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, @HeaderParam("construction") String construction) throws Exception {
        return Response
                .ok(_dispatch("arrays",
                        new Object[]{businessObject, mappingFile, construction}))
                .build();
    }

    @POST
    @Path("/esql-validation/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "esql-validation", template = "-h {workspace.home} -b {0} -m {1} -c {2}")
    public Response esqlValidation(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, String code) throws Exception {
        return Response
                .ok(_dispatch("esqlValidation",
                        new Object[]{businessObject, mappingFile, encodeMessage(code)}))
                .build();
    }

    @POST
    @Path("/construct-annotate/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "construct-annotate", template = "-h {workspace.home} -b {0} -m {1}")
    public Response constructAnnotate(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("constructAnnotate",
                        new Object[]{businessObject, mappingFile}))
                .build();
    }

    @POST
    @Path("/array-annotate/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "array-annotate", template = "-h {workspace.home} -b {0} -m {1}")
    public Response arrayAnnotate(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("arrayAnnotate",
                        new Object[]{businessObject, mappingFile}))
                .build();
    }

    @GET
    @Path("/xlsx-mappings/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "xlsx-mappings", template = "-h {workspace.home} -b {0} -f {1} -s {2}")
    public Response xlsxMappings(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, @HeaderParam("mappingSheet") String mappingSheet) throws Exception {
        return Response
                .ok(_dispatch("xlsxMappings",
                        new Object[]{businessObject, mappingFile, mappingSheet}))
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
    @CommandMapping(command = "xpath-mappings", template = "-h {workspace.home} -b {0} -m {1}")
    public Response xpathMappings(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("xpathMappings",
                        new Object[]{businessObject, mappingFile}))
                .build();
    }

    @GET
    @Path("/xpath-construct/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "xpath-construct", template = "-h {workspace.home} -b {0} -m {1} -c {2}")
    public Response xpathConstruct(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, @HeaderParam("construction") String construction) throws Exception {
        return Response
                .ok(_dispatch("xpathConstruct",
                        new Object[]{businessObject, mappingFile, construction}))
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
    @CommandMapping(command = "json-schema", template = "-h {workspace.home} -b {0} -x {1}")
    public Response jsonSchema(@PathParam("businessObject") String businessObject, @HeaderParam("jsonSchemaFile") String jsonSchemaFile) throws Exception {
        return Response
                .ok(_dispatch("jsonSchema",
                        new Object[]{businessObject, jsonSchemaFile}))
                .build();
    }

    @POST
    @Path("/filter-mapping/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "filter-mapping", template = "-h {workspace.home} -b {0} -m {1} -q {2}")
    public Response filterMapping(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, @HeaderParam("expression") String expression) throws Exception {
        return Response
                .ok(_dispatch("filterMapping",
                        new Object[]{businessObject, mappingFile, expression}))
                .build();
    }

    @POST
    @Path("/esql-gen/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "esql-gen", template = "-h {workspace.home} -b {0} -m {1} -c {2}")
    public Response esqlGen(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile, @HeaderParam("construction") String construction) throws Exception {
        return Response
                .ok(_dispatch("esqlGen",
                        new Object[]{businessObject, mappingFile, construction}))
                .build();
    }

    @GET
    @Path("/mappings-validation/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @CommandMapping(command = "mappings-validation", template = "-h {workspace.home} -b {0} -m {1}")
    public Response mappingsValidation(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("mappingsValidation",
                        new Object[]{businessObject, mappingFile}))
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
    @CommandMapping(command = "json-sample", template = "-h {workspace.home} -b {0} -x {1}")
    public Response jsonSample(@PathParam("businessObject") String businessObject, @HeaderParam("jsonSchemaFile") String jsonSchemaFile) throws Exception {
        return Response
                .ok(_dispatch("jsonSample",
                        new Object[]{businessObject, jsonSchemaFile}))
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
    @CommandMapping(command = "deprecated-mappings", template = "-h {workspace.home} -b {0} -m {1}")
    public Response deprecatedMappings(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("deprecatedMappings",
                        new Object[]{businessObject, mappingFile}))
                .build();
    }

}