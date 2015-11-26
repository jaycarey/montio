package com.jay.montior.controllers.common

import com.jay.montior.ci.*
import com.jay.montior.common.MontiorConfig
import com.jay.montior.common.OverviewView
import com.jay.montior.controllers.AbstractController
import com.jay.montior.core.StatusCache
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.memberProperties
import kotlin.text.RegexOption

open class HomeController @Inject constructor(
        private val montiorConfig: MontiorConfig,
        private val statusCache: StatusCache
) : AbstractController() {

    private val fakeBuilds by lazy { FakeBuilds() }

    fun overview(queryParams: Map<String, List<String>>): OverviewView {
        val builds = queryParams.get("fake")?.let { fakeBuilds.builds(it.first().toInt()) } ?: statusCache.builds()
        val filterParams = queryParams - "fake"
        val filteredBuilds = builds.filter { bs -> filterParams.isEmpty() || getFilters(filterParams).any { it(bs) } }
        val withSortedHistory = filteredBuilds.map { it.copy(lastTwenty = it.lastTwenty.sortedBy { it.first }) }
        return OverviewView(montiorConfig.env, withSortedHistory.sortedBy { "${it.buildType.projectId}-${it.buildType.name}" }.toList())
    }

    private fun getFilters(queryParams: Map<String, List<String>>): List<(BuildTypeStatus) -> Boolean> {
        val fields = queryParams.flatMap { pair ->
            pair.value.map { value -> { build: BuildTypeStatus -> filter(build, value, pair.key) } }
        }
        return fields
    }

    private fun filter(build: BuildTypeStatus, it: String, path: String): Boolean {
        val obj = buildChain(build, path.trimEnd('~').split("."))?.toString()
        return if (obj == null) false
        else if (path.endsWith("~")) obj.matches(it.toRegex(RegexOption.IGNORE_CASE))
        else obj.contains(it, ignoreCase = true)
    }

    private fun buildChain(obj: Any, parts: List<String>): Any? {
        if (!obj.javaClass.name.startsWith("com.jay.montior") || obj.javaClass.isEnum) return obj
        val properties = obj.javaClass.kotlin.memberProperties
        val matchingProperty = properties.firstOrNull { it.name == parts.firstOrNull() }
        if (matchingProperty == null) throw RuntimeException("No matching property at path")
        val nextObject = matchingProperty.get(obj)
        return nextObject?.let { buildChain(it, parts.drop(1)) }
    }
}

