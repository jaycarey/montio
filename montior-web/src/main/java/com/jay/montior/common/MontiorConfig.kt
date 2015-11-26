package com.jay.montior.common

open class MontiorConfig {
    val env: String = System.getProperty("env", "test")
    val url: String = System.getProperty("url", "https://teamcity.jetbrains.com")

}