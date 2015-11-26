package com.jay.montior.controllers.rest

import com.jay.montior.common.MontiorConfig
import com.jay.montior.common.Paths
import com.jay.montior.controllers.common.HomeController
import com.jay.montior.core.StatusCache
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path(Paths.Rest + Paths.Index)
public class RestHomeController @Inject constructor(
        montiorConfig: MontiorConfig,
        statusCache: StatusCache
) : HomeController(montiorConfig, statusCache) {

    @GET
    public fun boardRest(@Context uriInfo: UriInfo): Response =
            Response.ok(overview(uriInfo.getQueryParameters())).build()
}