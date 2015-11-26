package com.jay.montior.controllers.html

import com.jay.montior.common.MontiorConfig
import com.jay.montior.common.Paths
import com.jay.montior.common.ViewConstants
import com.jay.montior.controllers.common.HomeController
import com.jay.montior.core.StatusCache
import org.glassfish.jersey.server.mvc.Viewable
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@Singleton
@Produces(MediaType.TEXT_HTML)
@Path(Paths.Html + Paths.Index)
public class HtmlHomeController @Inject constructor(
        montiorConfig: MontiorConfig,
        statusCache: StatusCache
) : HomeController(montiorConfig, statusCache) {

    @GET
    public fun overviewHtml(@Context uriInfo: UriInfo): Viewable =
            Viewable(ViewConstants.Index, overview(uriInfo.getQueryParameters()))
}

