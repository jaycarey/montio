package com.jay.montior.core

import com.jay.montior.ci.*
import com.jay.montior.common.Util.callLogFailure
import jersey.repackaged.com.google.common.util.concurrent.FutureCallback
import jersey.repackaged.com.google.common.util.concurrent.Futures
import jersey.repackaged.com.google.common.util.concurrent.ListenableFuture
import jersey.repackaged.com.google.common.util.concurrent.MoreExecutors.listeningDecorator
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate

open class StatusCache(vararg val cis: Ci) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val pool by lazy { listeningDecorator(newFixedThreadPool(10)) }
    private val timer = Timer("check-timer", true)
    @Volatile private var checkFuture: ListenableFuture<Map<String, BuildTypeStatus>> = Futures.immediateFuture(mapOf())
    private val builds: ConcurrentHashMap<String, BuildTypeStatus> = ConcurrentHashMap()

    fun init() {
        timer.scheduleAtFixedRate(0, 3000, {
            if (checkFuture.isDone) {
                checkFuture = pool.submit(callLogFailure { checkBuilds() })
            }
        })
    }

    private fun checkBuilds(): Map<String, BuildTypeStatus> {
        val checks = cis.flatMap { ci ->
            ci.buildTypes.map { buildType ->
                val future: ListenableFuture<BuildTypeStatus?> = pool.submit(callLogFailure { ci.checkOrUnknown(buildType.value) })
                Futures.addCallback(future, object : FutureCallback<BuildTypeStatus?> {
                    override fun onSuccess(result: BuildTypeStatus?) {
                        if(result != null) builds.put (result.buildType.id, result)
                    }

                    override fun onFailure(t: Throwable?) {
                    }
                })
                future
            }
        }
        Futures.allAsList(checks).get(10, TimeUnit.MINUTES)
        return builds
    }

    private fun Ci.checkOrUnknown(buildType: BuildType): BuildTypeStatus? {
        try {
            return status(buildType)
        } catch (e: Exception) {
            logger.error("Unable to retrieve build's status $buildType, message: ${e.message}", e)
            return BuildTypeStatus(buildType, null, null, listOf())
        }
    }

    fun close() {
        timer.cancel()
        pool.shutdownNow()
    }

    open fun builds(): List<BuildTypeStatus> = builds.values.toList()

}