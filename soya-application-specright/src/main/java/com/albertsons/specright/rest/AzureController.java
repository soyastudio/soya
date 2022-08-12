package com.albertsons.specright.rest;

import com.albertsons.specright.service.AzureService;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

@Controller
@Path("/azure")
@Api(value = "Azure Blob Storage Service")
public class AzureController {

    @Autowired
    AzureService azure;

    @Autowired
    Specright specright;

    @GET
    @Path("/containers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response contains() {
        return Response.ok(azure.containerNames()).build();
    }

    @GET
    @Path("/blob-items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response blobItems(@QueryParam("container") String container, @QueryParam("prefix") String prefix) {
        return Response.ok(azure.listBlobs(container, prefix)).build();
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFromAzureBlobStorage(@HeaderParam("container") String container, @HeaderParam("fileName") String fileName) {
        try {
            byte[] csv = azure.readBlobFile(container, fileName);
            Response.ResponseBuilder builder = Response.ok(csv);
            builder.header("Content-Disposition", "attachment; filename=" + fileName);
            return builder.build();

        } catch (SpecrightException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadToAzureBlobStorage(@HeaderParam("container") String container, @HeaderParam("fileName") String fileName, String data) {
        try {
            if(fileName.toLowerCase().endsWith(".gz")) {
                byte[] raw = specright.gzip(data.getBytes(StandardCharsets.UTF_8));
                azure.writeBlobFile(raw, container, fileName);

            } else {
                azure.writeBlobFile(data.getBytes(StandardCharsets.UTF_8), container, fileName);
            }

            return Response.ok().build();

        } catch (SpecrightException e) {
            return Response.serverError().build();
        }
    }
}
