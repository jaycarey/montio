package com.jay.montior.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.jay.montior.ci.Ci
import com.jay.montior.ci.MockCi
import com.jay.montior.ci.teamcity.Guest
import com.jay.montior.ci.teamcity.TeamCityCi
import com.jay.montior.common.MapperFactory
import com.jay.montior.common.MontiorConfig
import com.jay.montior.common.Util.initAnd
import com.jay.montior.core.StatusCache
import org.glassfish.hk2.api.Factory
import org.glassfish.hk2.utilities.binding.AbstractBinder

public class MontiorBinder(val montiorConfig: MontiorConfig) : AbstractBinder() {

    val mapper = MapperFactory().mapper

    val ci = TeamCityCi(montiorConfig.url, montiorConfig.credentials)

    val statusCache = StatusCacheFactory(MockCi(ci))

    class StatusCacheFactory(ci: Ci) : Factory<StatusCache> {
        val statusCache by lazy {
            initAnd(StatusCache(ci)) {
                it.init()
            }
        }

        override fun provide(): StatusCache = statusCache
        override fun dispose(statusCache: StatusCache) = statusCache.close()
    }

    override fun configure() {
        bind(mapper).to(ObjectMapper::class.java)
        bind(montiorConfig).to(MontiorConfig::class.java)
        bindFactory(statusCache).to(StatusCache::class.java)
    }
}