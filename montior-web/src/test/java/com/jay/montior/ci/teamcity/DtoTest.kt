package com.jay.montior.ci.teamcity

import com.jay.montior.common.MapperFactory
import org.junit.Test
import com.fasterxml.jackson.module.kotlin.readValue
import org.hamcrest.MatcherAssert .*
import org.hamcrest.Matchers       .*

class DtoTest {

    private val TEST_URL = "http://tc.com/rest/buildTypes"
    private val ANOTHER_TEST_URL = "http://tc.com/rest/buildTypes/123"
    private val WEB_URL = "http://tc.com/buildTypes/123"

    private val objectMapper = MapperFactory().mapper

    @Test fun buildTypes() {
        val buildTypes = TcBuildTypes(TEST_URL, 1, listOf(
                TcBuildType(ANOTHER_TEST_URL, "123", "test", "test_build", "Test Build", "test_project", WEB_URL)
        ))

        val json = objectMapper.writeValueAsString(buildTypes)

        val deserialisedBuildTypes = objectMapper.readValue<TcBuildTypes>(json)

        assertThat(deserialisedBuildTypes, equalTo(buildTypes))
    }

    @Test fun buildTypesFromFile() {

        val deserialisedBuildTypes = objectMapper.readValue<TcBuildTypes>(read("/buildTypes.json"))

        assertThat(deserialisedBuildTypes, equalTo(
                TcBuildTypes("/guestAuth/app/rest/buildTypes", 2, listOf(
                        TcBuildType("/guestAuth/app/rest/buildTypes/id:bt297", "bt297", null, "Build", "Amazon API client", "AmazonApiClient", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt297"),
                        TcBuildType("/guestAuth/app/rest/buildTypes/id:bt296", "bt296", null, "Download missing jar", "Amazon API client", "AmazonApiClient", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt296")
                ))
        ))
    }

    @Test fun build() {
        val build = TcBuild(TEST_URL, "bt390", "success", 0, "Running build...", "running", "bt297", "",
                TcBuildType("/guestAuth/app/rest/buildTypes/id:bt297", "bt297", null, "Build", "Amazon API client", "AmazonApiClient", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt297"))

        val json = objectMapper.writeValueAsString(build)

        val deserialisedBuild = objectMapper.readValue<TcBuild>(json)

        assertThat(deserialisedBuild, equalTo(build))
    }

    @Test fun buildFromFile() {

        val deserialisedBuildTypes = objectMapper.readValue<TcBuild>(read("/build.json"))

        assertThat(deserialisedBuildTypes, equalTo(
                TcBuild("/guestAuth/app/rest/builds/id:632298", "632298", "SUCCESS", 0, "Tests passed: 1990, ignored: 1", "finished", "bt390", "https://teamcity.jetbrains.com/viewLog.html?buildId=632298&buildTypeId=bt390",
                        TcBuildType("/guestAuth/app/rest/buildTypes/id:bt390", "bt390", null, "Maven Build", "Kotlin", "Kotlin", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt390", false))
        ))
    }

    @Test fun builds() {
        val build = TcBuilds(
                TEST_URL, ANOTHER_TEST_URL, 1,
                listOf(TcBuild(TEST_URL,"bt390",  "success", 0, "Running build...", "running", "bt297", "",
                        TcBuildType("/guestAuth/app/rest/buildTypes/id:bt297", "bt297", null, "Build", "Amazon API client", "AmazonApiClient", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt297")))
        )

        val json = objectMapper.writeValueAsString(build)

        val deserialisedBuild = objectMapper.readValue<TcBuilds>(json)

        assertThat(deserialisedBuild, equalTo(build))
    }

    @Test fun buildsFromFile() {

        val deserialisedBuildTypes = objectMapper.readValue<TcBuilds>(read("/builds.json"))

        assertThat(deserialisedBuildTypes, equalTo(TcBuilds(
                "/guestAuth/app/rest/buildTypes/id:bt390/builds",
                "/guestAuth/app/rest/buildTypes/id:bt390/builds?locator=count:100,start:100",
                1,
                listOf(TcBuild("/guestAuth/app/rest/builds/id:632298", "632298", "SUCCESS", 0, null, "finished", "bt390", "https://teamcity.jetbrains.com/viewLog.html?buildId=632298&buildTypeId=bt390", null))
        )))
    }

    private fun read(path: String): String =
            javaClass.getResourceAsStream(path).bufferedReader().readText()
}