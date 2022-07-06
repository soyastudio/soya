package soya.framework.albertsons.restapi;

import io.swagger.annotations.Api;
import soya.framework.action.dispatch.ActionForward;
import soya.framework.action.dispatch.ActionDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bod")
@Api(value = "bod")
public class BodController extends ActionDispatcher {

    public BodController() {
        super();
    }

    @POST
    @Path("/mappings-override/{businessObject}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ActionForward(command = "mappings-override -h {workspace.home} -b {0} -m {1} -o {2}")
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
    @ActionForward(command = "arrays -h {workspace.home} -b {0} -m {1} -c {2}")
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
    @ActionForward(command = "esql-validation -h {workspace.home} -b {0} -m {1} -c {2}")
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
    @ActionForward(command = "construct-annotate -h {workspace.home} -b {0} -m {1}")
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
    @ActionForward(command = "array-annotate -h {workspace.home} -b {0} -m {1}")
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
    @ActionForward(command = "xlsx-mappings -h {workspace.home} -b {0} -f {1} -s {2}")
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
    @ActionForward(command = "schema -h {workspace.home} -b {0}")
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
    @ActionForward(command = "sample-xml -h {workspace.home} -b {0}")
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
    @ActionForward(command = "xpath-mappings -h {workspace.home} -b {0} -m {1}")
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
    @ActionForward(command = "xpath-construct -h {workspace.home} -b {0} -m {1} -c {2}")
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
    @ActionForward(command = "create -h {workspace.home} -b {0}")
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
    @ActionForward(command = "json-types -h {workspace.home} -b {0}")
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
    @ActionForward(command = "json-schema -h {workspace.home} -b {0} -x {1}")
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
    @ActionForward(command = "filter-mapping -h {workspace.home} -b {0} -m {1} -q {2}")
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
    @ActionForward(command = "esql-gen -h {workspace.home} -b {0} -m {1} -c {2}")
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
    @ActionForward(command = "mappings-validation -h {workspace.home} -b {0} -m {1}")
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
    @ActionForward(command = "avsc -h {workspace.home} -b {0}")
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
    @ActionForward(command = "json-type-mappings -h {workspace.home} -b {0}")
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
    @ActionForward(command = "json-sample -h {workspace.home} -b {0} -x {1}")
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
    @ActionForward(command = "sample-avro -h {workspace.home} -b {0}")
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
    @ActionForward(command = "deprecated-mappings -h {workspace.home} -b {0} -m {1}")
    public Response deprecatedMappings(@PathParam("businessObject") String businessObject, @HeaderParam("mappingFile") String mappingFile) throws Exception {
        return Response
                .ok(_dispatch("deprecatedMappings",
                        new Object[]{businessObject, mappingFile}))
                .build();
    }

}