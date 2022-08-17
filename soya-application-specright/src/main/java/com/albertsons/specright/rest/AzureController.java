package com.albertsons.specright.rest;

import com.albertsons.specright.service.AzureService;
import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightException;
import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
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

    @POST
    @Path("/container/create")
    public Response createContainer(@HeaderParam("name") String name) {
        azure.createContainer(name);
        return Response.ok().build();
    }

    @DELETE
    @Path("/container/delete")
    public Response deleteContainer(@HeaderParam("name") String name) {
        azure.deleteContainer(name);
        return Response.ok().build();
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
    public Response download(@HeaderParam("container") String container, @HeaderParam("fileName") String fileName) {
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
    @Path("/write")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response write(@HeaderParam("container") String container, @HeaderParam("fileName") String fileName, String data) {
        try {
            if (fileName.toLowerCase().endsWith(".gz")) {
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


    @POST
    @Path("/upload")
    public Response upload(@HeaderParam("container") String container,
                           @HeaderParam("fileName") String fileName,
                           @FormDataParam("upload") InputStream is) {

        try {
            byte[] data = StreamUtils.copyToByteArray(is);


            if (fileName.toLowerCase().endsWith(".gz")) {
                byte[] raw = specright.gzip(data);
                azure.writeBlobFile(raw, container, fileName);

            } else {
                azure.writeBlobFile(data, container, fileName);
            }

            return Response.ok().build();

        } catch (IOException | SpecrightException e) {
            return Response.serverError().build();
        }
    }


    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBlob(@HeaderParam("container") String container, @HeaderParam("fileName") String fileName) {
        boolean deleted = azure.deleteBlobFile(container, fileName);
        return Response.ok(deleted).build();
    }

    @DELETE
    @Path("/delete-all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAll(@HeaderParam("container") String container, @HeaderParam("prefix") String prefix) {
        azure.deleteAll(container, prefix);
        return Response.ok().build();
    }
}
