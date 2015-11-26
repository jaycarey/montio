package com.jay.montior.ci.teamcity

import com.jay.montior.ci.*
import com.jay.montior.common.MapperFactory
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature.basic
import org.glassfish.jersey.jackson.JacksonFeature
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType

data class Credentials(val username: String, val password: String)

val Guest = Credentials("guest", "")

class TeamCityCi(url: String, credentials: Credentials) : Ci {

    private val client by lazy {
        ClientBuilder.newClient()
                .register(MapperFactory::class.java)
                .register(JacksonFeature::class.java)
                .register(basic(credentials.username, credentials.password))
    }

    private val target = client.target(url)
            .path(if (credentials == Guest) "guestAuth" else "httpAuth")
            .path("app")
            .path("rest")

    override fun ping(): String = target
            .request(MediaType.TEXT_PLAIN)
            .get(String::class.java)

    override val buildTypes: Map<String, BuildType> by lazy {
        toBuildTypes(target
                .path("buildTypes")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TcBuildTypes::class.java))
                .toMapBy { it.id }
    }

    override fun status(buildType: BuildType): BuildTypeStatus? {
        val builds = toBuilds(target
                .path("buildTypes")
                .path("id:${buildType.id}")
                .path("builds")
                .queryParam("locator", "running:any,count:50")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TcBuilds::class.java))
        return BuildTypeStatus(
                buildType = buildType,
                latestFinished = builds.firstOrNull { it.state == State.finished },
                latestRunning = builds.firstOrNull { it.state == State.running }?.let { status(it) },
                lastTwenty = builds.map { it.id to it.status})
    }

    override fun status(build: Build): Build? {
        return toBuild(target
                .path("builds")
                .path("id:${build.id}")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TcBuild::class.java))
    }

    override fun status(since: ZonedDateTime): List<Build> {
        return toBuilds(target
                .path("builds")
                .queryParam("locator", "sinceDate:${since.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssZ"))}")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(TcBuilds::class.java))
    }

    private fun toBuilds(builds: TcBuilds): List<Build> =
            builds.build?.map { toBuild(it) } ?: listOf()

    private fun toBuild(build: TcBuild): Build =
            Build(
                    id = build.id,
                    buildType = build.buildType?.let { toBuildType(it) } ?: buildTypes.getRaw(build.buildTypeId),
                    status = toBuildStatus(build.status),
                    text = build.statusText,
                    percentageComplete = build.percentageComplete,
                    state = toBuild(build.state),
                    webUrl = build.webUrl
            )

    private fun toBuildStatus(status: String): Status = when (status) {
        "SUCCESS" -> Status.success
        "FAILURE" -> Status.failed
        "ERROR" -> Status.error
        else -> Status.unknown
    }

    private fun toBuild(state: String): State = when (state) {
        "running" -> State.running
        "finished" -> State.finished
        else -> State.unknown
    }

    private fun toBuildTypes(buildType: TcBuildTypes): List<BuildType> =
            buildType.buildType.map { toBuildType(it) }

    private fun toBuildType(it: TcBuildType) =
            BuildType(it.id, it.name, it.projectId, it.projectName, it.webUrl)

}