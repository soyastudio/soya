package com.albertsons.specright.rest;

import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/specright")
@Api(value = "Specright API Scan Service")
public class SpecrightRestAPI {

    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scan(String pipeline) {
        return Response.ok().build();
    }
}
