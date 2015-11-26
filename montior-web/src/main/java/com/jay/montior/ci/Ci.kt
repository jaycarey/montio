package com.jay.montior.ci

import com.fasterxml.jackson.core.type.TypeReference
import com.jay.montior.common.MapperFactory
import java.io.File
import java.time.ZonedDateTime


interface Ci {

    val buildTypes: Map<String, BuildType>

    fun ping(): String

    fun status(buildType: BuildType): BuildTypeStatus?

    fun status(since: ZonedDateTime): List<Build>
    open fun status(build: Build): Build?
}

class MockCi(private val ci: Ci, private val cache: Boolean = false) : Ci {
    private val offlineDir = "${System.getProperty("user.home")}/.montior/offline"

    private val mapper = MapperFactory().mapper

    override val buildTypes: Map<String, BuildType>
        get() {
            return process("buildTypes.json") { ci.buildTypes }
        }

    override fun ping(): String {
        return process("ping.json") { ci.ping() }
    }

    override fun status(buildType: BuildType): BuildTypeStatus? {
        return process("buildTypeStatus-${buildType.id}.json") { ci.status(buildType) }
    }

    override fun status(build: Build): Build? {
        return process("build-${build.id}.json") { ci.status(build) }
    }

    override fun status(since: ZonedDateTime): List<Build> {
        return listOf()
    }

    private inline fun <reified T : Any?> process(name: String, method: () -> T): T {
        if (cache) {
            return cache(method, name)
        } else try {
            return mapper.readValue(File("$offlineDir/$name"), object : TypeReference<T>() {})
        } catch (e: Exception) {
            return cache(method, name)
        }
    }

    private inline fun <reified T : Any?> cache(method: () -> T, name: String): T {
        val value = method()
        mapper.writeValue(File("$offlineDir/$name"), value)
        return value
    }

}