package com.jay.montior.controllers.common

import com.jay.montior.ci.*
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FakeBuilds {

    private val nouns = javaClass.getResourceAsStream("/nouns.csv").bufferedReader(Charset.defaultCharset()).lineSequence().toList()
    private val random = Random()
    private val pool by lazy { Executors.newScheduledThreadPool(20) }
    private val builds: ConcurrentHashMap<String, BuildTypeStatus> = ConcurrentHashMap()
    private var project: Pair<String, Int>? = null

    private var ids = 0
    private var buildIds = 0

    fun builds(count: Int): List<BuildTypeStatus> {
        while (builds.size < count) {
            if (project == null || random.nextInt(3) == 0) project = randomNoun() to ((project?.second ?: 0) + 1)
            val name = randomNoun()
            ids++
            val buildType = BuildType("build-$ids", name, "project-${project?.second}", project!!.first, "http://google.com?q=${name}")
            val buildTypeStatus = BuildTypeStatus(buildType, null, null, listOf())
            builds.put("build-$ids", buildTypeStatus)
            schedule(buildType.id, 0)
        }
        return builds.values.take(count ).toList()
    }

    private fun runBuild(id: String): Unit {
        var percentComplete = 0.0
        val increment = 5 + random.nextDouble() * 50
        val buildTypeStatus = builds[id]!!
        var running = Build("build-${buildIds++}", buildTypeStatus.buildType, Status.success, randomNoun(), percentComplete.toInt(), State.running, buildTypeStatus.buildType.webUrl)
        val failAt = random.nextInt(200)
        while (percentComplete < 100) {
            percentComplete = Math.min(100.0, percentComplete + increment)
            running = running.copy(percentageComplete = percentComplete.toInt(), status = if (percentComplete > failAt) Status.failed else Status.success)
            builds.put(buildTypeStatus.buildType.id, buildTypeStatus.copy(latestRunning = running))
            Thread.sleep(1000)
        }
        builds.put(buildTypeStatus.buildType.id, buildTypeStatus.copy(
                latestRunning = null,
                latestFinished = running.copy(state = State.finished),
                lastTwenty = buildTypeStatus.lastTwenty + (running.id to running.status)))
        schedule(id, 20)
    }

    private fun schedule(id: String, delay: Int) {
        pool.schedule({ runBuild(id) }, delay + Math.abs(random.nextLong()) % 30L, TimeUnit.SECONDS)
    }

    private fun randomNoun(): String =
            nouns[random.nextInt(nouns.size)].capitalize()

}