package com.jay.montior.common

import com.jay.montior.core.StatusCache
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

object Util : UtilClass()

open class UtilClass {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun initAnd(statusCache: StatusCache, and: (StatusCache) -> Unit): StatusCache {
        and(statusCache)
        return statusCache
    }

    fun <T> callLogFailure(function: () -> T): Callable<T> {
        return object : Callable<T> {
            override fun call(): T = try {
                function()
            } catch (e: Exception) {
                logger.error("Error checking builds", e)
                throw e
            }
        }
    }

}
