package com.jay.montior.common

import com.jay.montior.ci.teamcity.Credentials

val Test = "test"

open class MontiorConfig {
    val env: String = System.getProperty("env", Test)
    val url: String = System.getProperty("ci.url", "https://teamcity.jetbrains.com")
    val credentials: Credentials = Credentials(
            System.getProperty("ci.username", "guest"),
            System.getProperty("ci.passowrd", ""))

}