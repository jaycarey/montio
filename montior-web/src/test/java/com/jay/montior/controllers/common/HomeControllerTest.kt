package com.jay.montior.controllers.common

import com.jay.montior.ci.*
import com.jay.montior.ci.Status.success
import com.jay.montior.common.MontiorConfig
import com.jay.montior.core.StatusCache
import com.jay.montior.test.TestUtil
import com.jay.montior.test.TestUtil.equalTo
import com.jay.montior.test.TestUtil.returns
import org.junit.Test

class HomeControllerTest {

    private val mockMonitorConfig = MontiorConfig()

    private val mockStatusCache by TestUtil.lazyMock<StatusCache>()

    private val homeController by lazy { HomeController(mockMonitorConfig, mockStatusCache) }

    @Test fun canFilterBuildUsingField() {

        mockStatusCache.builds() returns listOf(
                buildType("bt390", success),
                buildType("bt0", success)
        )

        homeController.overview(mapOf("latestFinished.status" to listOf("success"))).builds equalTo listOf(
                buildType("bt0", success),
                buildType("bt390", success)
        )
    }

    @Test fun canFilterBuildUsingSubElement() {

        mockStatusCache.builds() returns listOf(
                buildType("bt390", success),
                buildType("bt0", success)
        )

        homeController.overview(mapOf("buildType.id" to listOf("bt390"))).builds equalTo listOf(
                buildType("bt390", success)
        )
    }

    @Test fun canFilterBuildUsingRegex() {

        mockStatusCache.builds() returns listOf(
                buildType("bt390", success),
                buildType("btABC", success),
                buildType("bt0", success)
        )

        homeController.overview(mapOf("buildType.id~" to listOf("bt[0-9]+"))).builds equalTo listOf(
                buildType("bt0", success),
                buildType("bt390", success)
        )
    }

    private fun buildType(id: String, status: Status): BuildTypeStatus {
        val buildType = BuildType(id, "$id-name", "$id-project", "$id-project-name", "")
        return BuildTypeStatus(buildType, Build("$id-build", buildType, status, "Running build...", 0, State.finished, ""), Build("$id-build", buildType, status, "Running build...", 0, State.running, ""), listOf())
    }
}

