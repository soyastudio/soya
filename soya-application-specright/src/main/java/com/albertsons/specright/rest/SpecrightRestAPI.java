package com.albertsons.specright.rest;

import com.albertsons.specright.service.Specright;
import com.albertsons.specright.service.SpecrightException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Controller
@Path("/com/albertsons/specright")
@Api(value = "Specright API Scan Service")
public class SpecrightRestAPI {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    Specright specright;

    @GET
    @Path("/scanners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scanners() {
        return Response.ok(GSON.toJson(specright.scanners())).build();
    }

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchToken() {
        try {
            return Response.ok(GSON.toJson(specright.fetchToken())).build();

        } catch (SpecrightException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/scan")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scan(@HeaderParam("scanner") String scanner, @HeaderParam("token") String token) {
        try {
            return Response.ok(GSON.toJson(specright.bulkJob(scanner, token))).build();

        } catch (SpecrightException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/job-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jobStatus(@HeaderParam("job-id") String jobId, @HeaderParam("token") String token) {
        try {
            return Response.ok(GSON.toJson(specright.jobStatus(jobId, token))).build();

        } catch (SpecrightException e) {
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
