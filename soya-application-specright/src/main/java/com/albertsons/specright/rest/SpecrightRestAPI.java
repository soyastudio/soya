package com.albertsons.specright.rest;

import com.albertsons.specright.service.Specright;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/specright")
@Api(value = "Specright API Scan Service")
public class SpecrightRestAPI {
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/scanners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scan() {


        return Response.ok(GSON.toJson(Specright.getInstance().scanners())).build();
    }
}
