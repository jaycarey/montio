package com.jay.montior.webapp

import com.jay.montior.common.MontiorConfig
import com.jay.montior.controllers.AbstractController
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.server.filter.UriConnegFilter
import java.util.*
import javax.ws.rs.core.MediaType

class MontiorResourceConfig @JvmOverloads constructor(
        montiorConfig: MontiorConfig = MontiorConfig(),
        val binder: MontiorBinder = MontiorBinder(montiorConfig)
) : ResourceConfig() {

    init {
        // Stops AWT creating an annoying icon.
        System.setProperty("java.awt.headless", "true");

        packages(AbstractController::class.java.`package`.name)
        register(binder)
        enableJsonContentNegotiation()
        enableJacksonJson()
        register(com.jay.montior.webapp.ExceptionMapper::class.java)
        enableJspSupport()
        enableMultipartSupport()
    }

    private fun enableJspSupport() {
        register(org.glassfish.jersey.server.mvc.jsp.JspMvcFeature::class.java)
        property(org.glassfish.jersey.server.mvc.jsp.JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/jsps/")
        property(org.glassfish.jersey.servlet.ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/(favicon.png|css|img|js|fonts|WEB-INF/jsps)/.*")
    }

    private fun enableMultipartSupport() {
        register(MultiPartFeature::class.java)
    }

    private fun enableJacksonJson() {
        val jacksonJaxbProvider = com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider()
        jacksonJaxbProvider.setMapper(binder.mapper)
        register(jacksonJaxbProvider)
    }

    private fun enableJsonContentNegotiation() {
        register(UriConnegFilter::class.java)
        val mappedMediaTypes = HashMap<String, MediaType>(1, 1f)
        mappedMediaTypes.put("json", MediaType.APPLICATION_JSON_TYPE)
        property(ServerProperties.MEDIA_TYPE_MAPPINGS, mappedMediaTypes)
    }
}

