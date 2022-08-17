package com.albertsons.specright.rest;

import com.albertsons.specright.service.AzureService;
import com.albertsons.specright.service.Configuration;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Controller
@Path("/specright")
@Api(value = "Specright API Scan Service")
public class SpecrightController {

    @Autowired
    Specright specright;

    @Autowired
    AzureService azureService;

    @GET
    @Path("/scanners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scanners() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("heartbeat", specright.getSequence());

        JsonArray array = new JsonArray();

        specright.scanners().forEach(e -> {
            JsonObject scanner = new JsonObject();
            scanner.addProperty("name", e);
            if(specright.getLastScannedTimestamp(e) != null) {
                scanner.addProperty("lastScannedTime", Specright.DATE_FORMAT.format(new Date(specright.getLastScannedTimestamp(e))));
            }

            array.add(scanner);
        });

        jsonObject.add("scanner", array);

        return Response.ok(Specright.GSON.toJson(jsonObject)).build();
    }

    @POST
    @Path("/scan/{scanner}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response scan(@PathParam("scanner") String scanner, @HeaderParam("type") String type) {
        String ext = type == null ? "csv" : type;
        String dir = Configuration.get(Configuration.AZURE_BLOB_STORAGE_BASE_DIR) + "/" + scanner;
        String fileName = scanner + "_" + System.currentTimeMillis() + "." + ext;

        try {
            String token = specright.fetchToken();
            String jobId = specright.scan(scanner, token);
            Thread.sleep(1500l);
            byte[] data = specright.jobDetails(jobId, token);

            data = specright.csvFilter(scanner, data);

            if (ext.equalsIgnoreCase("GZ")) {
                data = specright.gzip(data);
                azureService.writeBlobFile(data, Configuration.get(Configuration.AZURE_BLOB_STORAGE_CONTAINER_NAME), dir + "/" + fileName);

            } else {

            }

            Response.ResponseBuilder builder = Response.ok(data);
            builder.header("Content-Disposition", "attachment; filename=" + fileName);
            return builder.build();

        } catch (SpecrightException | InterruptedException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate() {
        try {
            return Response.ok(specright.authenticate()).build();

        } catch (SpecrightException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fetchToken() {
        try {
            return Response.ok(specright.fetchToken()).build();

        } catch (SpecrightException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/bulk-job")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bulkJob(@HeaderParam("scanner") String scanner, @HeaderParam("token") String token) {
        try {
            return Response.ok(specright.bulkJob(scanner, token)).build();

        } catch (SpecrightException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/scan-job")
    @Produces(MediaType.TEXT_PLAIN)
    public Response scanJob(@HeaderParam("scanner") String scanner, @HeaderParam("token") String token) {
        try {
            return Response.ok(specright.scan(scanner, token)).build();

        } catch (SpecrightException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/job-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jobStatus(@HeaderParam("job-id") String jobId, @HeaderParam("token") String token) {
        try {
            return Response.ok(specright.jobStatus(jobId, token)).build();

        } catch (SpecrightException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/job-details")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response fetchToken(@HeaderParam("job-id") String jobId, @HeaderParam("token") String token) {
        try {
            byte[] csv = specright.jobDetails(jobId, token);
            Response.ResponseBuilder builder = Response.ok(csv);
            builder.header("Content-Disposition", "attachment; filename=" + jobId + ".csv");
            return builder.build();

        } catch (SpecrightException e) {
            return Response.serverError().build();
        }
    }
}
