package com.jay.montior.webapp

import com.jay.montior.common.ExceptionView
import com.jay.montior.common.MontiorConfig
import com.jay.montior.common.ViewConstants
import org.glassfish.jersey.server.ContainerException
import org.glassfish.jersey.server.mvc.Viewable
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType.TEXT_HTML_TYPE
import javax.ws.rs.core.MediaType.WILDCARD_TYPE
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

public class ExceptionMapper @Inject constructor(private val httpHeaders: HttpHeaders, private val config: MontiorConfig) : ExceptionMapper<Exception> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun toResponse(exception: Exception): Response? {
        if (exception is InternalServerErrorException || exception is ContainerException) {
            logger.error("Unexpected Error.", exception)
        }

        val acceptableMediaTypes = httpHeaders.acceptableMediaTypes
        if (acceptableMediaTypes.contains(TEXT_HTML_TYPE) || acceptableMediaTypes.contains(WILDCARD_TYPE)) {
            return Response.status(500)
                    .entity(Viewable(ViewConstants.Error, exceptionDto(exception)))
                    .type(TEXT_HTML_TYPE)
                    .build()
        } else {
            return Response.status(500)
                    .entity(exceptionDto(exception))
                    .build()
        }
    }

    private fun exceptionDto(exception: Exception) =
            ExceptionView(config.env, exception.message.orEmpty(), stackTraceToString(exception))

    private fun stackTraceToString(exception: Exception) =
            exception.stackTrace.map { it.toString() }

}

