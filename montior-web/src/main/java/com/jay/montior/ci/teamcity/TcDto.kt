package com.jay.montior.ci.teamcity

interface TcDto {
    val href: String
}

data class TcBuildTypes(
        override val href: String,
        val count: Int,
        val buildType: List<TcBuildType>
) : TcDto

data class TcBuildType(
        override val href: String,
        val id: String,
        val description: String?,
        val name: String,
        val projectName: String,
        val projectId: String,
        val webUrl: String,
        val paused: Boolean = false
) : TcDto

data class TcBuilds(
        override val href: String,
        val nextHref: String?,
        val count: Int,
        val build: List<TcBuild>?
) : TcDto

data class TcBuild(
        override val href: String,
        val id: String,
        val status: String,
        val percentageComplete: Int,
        val statusText: String?,
        val state: String,
        val buildTypeId: String?,
        val webUrl: String?,
        val buildType: TcBuildType?
) : TcDto