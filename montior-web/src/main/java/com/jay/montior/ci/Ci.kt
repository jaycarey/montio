package com.jay.montior.ci

import java.time.ZonedDateTime

interface Ci {

    val buildTypes: Map<String, BuildType>

    fun ping(): String

    fun status(buildType: BuildType): BuildTypeStatus?

    fun status(since: ZonedDateTime): List<Build>
    open fun status(build: Build): Build?
}

