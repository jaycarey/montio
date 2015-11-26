package com.jay.montior.ci

import com.jay.montior.ci.teamcity.Guest
import com.jay.montior.ci.teamcity.TeamCityCi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

abstract class CiTest<T : Ci>() {

    private val KotlinBuild = BuildType("bt390", "Maven Build", "Kotlin", "Kotlin", "https://teamcity.jetbrains.com/viewType.html?buildTypeId=bt390")

    abstract val ci: T

    @Test fun canTalkToCi() {
        assertThat(ci.ping(), containsString("TeamCity REST API"));
    }

    @Test fun canGetBuildTypes() {
        assertThat(ci.buildTypes.values, hasItem(KotlinBuild));
    }

    @Test fun canGetBuildStatuses() {
        val status = ci.status(KotlinBuild)
        assertThat(status, equalTo( BuildTypeStatus(KotlinBuild, Build("633393", KotlinBuild, Status.success, null, 0, State.finished, "https://teamcity.jetbrains.com/viewLog.html?buildId=633393&buildTypeId=bt390"), null, listOf())));
    }

    @Test fun canGetBuildsSince() {
//        val status = ci.status(ZonedDateTime.now().minusMinutes(1))
//        assertThat(status.size, greaterThan(-1));
    }
}

class TeamCityCiTest() : CiTest<TeamCityCi>() {
    override val ci = TeamCityCi("https://teamcity.jetbrains.com", Guest)
}

