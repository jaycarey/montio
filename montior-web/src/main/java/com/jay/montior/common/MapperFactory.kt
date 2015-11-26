package com.jay.montior.common

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.math.BigInteger
import javax.ws.rs.ext.ContextResolver

class MapperFactory : ContextResolver<ObjectMapper> {

    val entropyModule = SimpleModule("KethModule")

    val mapper = ObjectMapper()
            .registerKotlinModule()
            .registerModule(Jdk8Module())
            .registerModule(entropyModule)
            .registerModule(JavaTimeModule())
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.IGNORE_UNDEFINED)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)

    override fun getContext(type: Class<*>): ObjectMapper =
            mapper

}