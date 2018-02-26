package org.zicat.service.template;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @ This is a demo
 * Created by lz31 on 2017/10/25.
 */
@Api(value = "/v1")
@Path("/v1")
public class HelloWorldController {

    @ApiOperation(value = "Query"
            , httpMethod = "GET")
    @ApiResponses({
            @ApiResponse(message = "Process Successs", code = 200, response = HelloWorldResponse.class)
    })

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public HelloWorldResponse helloWorld(@QueryParam(value = "name") String name) {
        return new HelloWorldResponse(name, 200);
    }
}
